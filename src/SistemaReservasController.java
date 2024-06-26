import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

class SistemaReservasController {
    private Connection connection;
    private SimpleDateFormat dateTimeFormat;

    public SistemaReservasController() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_reservas", "root", "asaelsimone"); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    public List<Mesa> getMesas() {
        List<Mesa> mesas = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM mesas");
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("id"), rs.getInt("capacidad"));
                mesa.setDisponible(rs.getBoolean("disponible"));
                mesas.add(mesa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesas;
    }

    public List<Reserva> getReservas() {
        List<Reserva> reservas = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM reservas");
            while (rs.next()) {
                Usuario usuario = getUsuarioById(rs.getInt("usuario_id"));
                Mesa mesa = getMesaById(rs.getInt("mesa_id"));
                Calendar fechaHora = Calendar.getInstance();
                fechaHora.setTime(rs.getTimestamp("fecha_hora"));
                Reserva reserva = new Reserva(rs.getInt("id"), usuario, mesa, fechaHora);
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    private Usuario getUsuarioById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM usuarios WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Mesa getMesaById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM mesas WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("id"), rs.getInt("capacidad"));
                mesa.setDisponible(rs.getBoolean("disponible"));
                return mesa;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hacerReserva(String nombreComensal, String emailComensal, int mesaId, String fechaHoraIn) throws Exception {
        Calendar fechaHora = Calendar.getInstance();
        fechaHora.setTime(dateTimeFormat.parse(fechaHoraIn));
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, 1); // Incrementa la hora actual en una hora

        if (fechaHora.before(now)) {
            throw new Exception("No se puede realizar una reserva menos de una hora en el futuro.");
        } else {
            try (PreparedStatement userStmt = connection.prepareStatement("INSERT INTO usuarios (nombre, email) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement reservaStmt = connection.prepareStatement("INSERT INTO reservas (usuario_id, mesa_id, fecha_hora) VALUES (?, ?, ?)");
                 PreparedStatement mesaStmt = connection.prepareStatement("UPDATE mesas SET disponible = ? WHERE id = ?")) {

                userStmt.setString(1, nombreComensal);
                userStmt.setString(2, emailComensal);
                userStmt.executeUpdate();
                ResultSet rs = userStmt.getGeneratedKeys();
                int userId = 0;
                if (rs.next()) {
                    userId = rs.getInt(1);
                }

                reservaStmt.setInt(1, userId);
                reservaStmt.setInt(2, mesaId);
                reservaStmt.setTimestamp(3, new Timestamp(fechaHora.getTimeInMillis()));
                reservaStmt.executeUpdate();

                mesaStmt.setBoolean(1, false);
                mesaStmt.setInt(2, mesaId);
                mesaStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void modificarReserva(int reservaId, String fechaHoraIn) throws Exception {
        Calendar fechaHora = Calendar.getInstance();
        fechaHora.setTime(dateTimeFormat.parse(fechaHoraIn));
        try (PreparedStatement stmt = connection.prepareStatement("UPDATE reservas SET fecha_hora = ? WHERE id = ?")) {
            stmt.setTimestamp(1, new Timestamp(fechaHora.getTimeInMillis()));
            stmt.setInt(2, reservaId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo actualizar la reserva, id no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al modificar la reserva: " + e.getMessage());
        }
    }

    public void eliminarReserva(int reservaId) throws Exception {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM reservas WHERE id = ?");
             PreparedStatement mesaStmt = connection.prepareStatement("UPDATE mesas SET disponible = ? WHERE id = ?")) {

            // Obtener la reserva antes de eliminarla
            Reserva reserva = getReservaById(reservaId);
            if (reserva == null) {
                throw new SQLException("Reserva no encontrada, id: " + reservaId);
            }

            // Eliminar la reserva
            stmt.setInt(1, reservaId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo eliminar la reserva, id no encontrado.");
            }

            // Actualizar el estado de la mesa
            mesaStmt.setBoolean(1, true);
            mesaStmt.setInt(2, reserva.getMesa().getId());
            mesaStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al eliminar la reserva: " + e.getMessage());
        }
    }

    private Reserva getReservaById(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM reservas WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = getUsuarioById(rs.getInt("usuario_id"));
                Mesa mesa = getMesaById(rs.getInt("mesa_id"));
                Calendar fechaHora = Calendar.getInstance();
                fechaHora.setTime(rs.getTimestamp("fecha_hora"));
                return new Reserva(rs.getInt("id"), usuario, mesa, fechaHora);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
