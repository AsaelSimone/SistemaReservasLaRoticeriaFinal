import java.util.*;
import java.text.SimpleDateFormat;

class Reserva {
    private int id;
    private Usuario usuario;
    private Mesa mesa;
    private Calendar fechaHora;

    public Reserva(int id, Usuario usuario, Mesa mesa, Calendar fechaHora) {
        this.id = id;
        this.usuario = usuario;
        this.mesa = mesa;
        this.fechaHora = fechaHora;
        mesa.setDisponible(false);
    }

    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Mesa getMesa() { return mesa; }
    public Calendar getFechaHora() { return fechaHora; }
    public void setFechaHora(Calendar fechaHora) { this.fechaHora = fechaHora; }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "Reserva ID: " + id + " para " + usuario.getNombre() + " en mesa " + mesa.getId() + " el " + dateFormat.format(fechaHora.getTime());
    }
}
