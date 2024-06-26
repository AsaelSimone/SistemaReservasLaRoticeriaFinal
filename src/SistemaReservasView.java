import java.util.InputMismatchException;
import java.util.Scanner;

public class SistemaReservasView {
    private static SistemaReservasController controller = new SistemaReservasController();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n1. Hacer Reserva");
            System.out.println("2. Modificar Reserva");
            System.out.println("3. Eliminar Reserva");
            System.out.println("4. Verificar Disponibilidad");
            System.out.println("5. Mostrar Reservas");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();  // Consume nueva linea sobrante

                switch (opcion) {
                    case 1:
                        hacerReserva();
                        break;
                    case 2:
                        modificarReserva();
                        break;
                    case 3:
                        eliminarReserva();
                        break;
                    case 4:
                        verificarDisponibilidad();
                        break;
                    case 5:
                        mostrarReservas();
                        break;
                    case 6:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida, intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Consume la entrada incorrecta y evita un bucle infinito
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void verificarDisponibilidad() {
        System.out.println("Estado actual de las mesas:");
        for (Mesa mesa : controller.getMesas()) {
            System.out.println("Mesa " + mesa.getId() + " para " + mesa.getCapacidad() + " personas - " +
                               (mesa.isDisponible() ? "Disponible" : "No disponible"));
        }
    }

    private static void hacerReserva() {
        try {
            System.out.println("Ingrese el nombre del comensal:");
            String nombreComensal = scanner.nextLine();
            System.out.println("Ingrese el email del comensal:");
            String emailComensal = scanner.nextLine();

            System.out.println("Seleccione una mesa disponible:");
            for (int i = 0; i < controller.getMesas().size(); i++) {
                if (controller.getMesas().get(i).isDisponible()) {
                    System.out.println((i + 1) + ". Mesa " + controller.getMesas().get(i).getId() + " para " + controller.getMesas().get(i).getCapacidad() + " personas");
                }
            }
            int choiceMesa = scanner.nextInt();
            scanner.nextLine();  // Consume newline left-over

            System.out.println("Ingrese la fecha y hora de la reserva (dd/MM/yyyy HH:mm):");
            String fechaHoraIn = scanner.nextLine();

            controller.hacerReserva(nombreComensal, emailComensal, choiceMesa, fechaHoraIn);
            System.out.println("Reserva realizada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al hacer la reserva: " + e.getMessage());
        }
    }

    private static void modificarReserva() {
        if (controller.getReservas().isEmpty()) {
            System.out.println("No hay reservas para modificar.");
            return;
        }
        try {
            System.out.println("Ingrese la ID de la reserva que desea modificar:");
            for (int i = 0; i < controller.getReservas().size(); i++) {
                System.out.println((i + 1) + ". " + controller.getReservas().get(i));
            }
            int choiceReserva = scanner.nextInt();
            scanner.nextLine();  // Consumir el resto de la línea

            System.out.println("Ingrese la nueva fecha y hora de la reserva (dd/MM/yyyy HH:mm):");
            String fechaHoraIn = scanner.nextLine();

            controller.modificarReserva(choiceReserva, fechaHoraIn);
            System.out.println("Reserva modificada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al modificar la reserva: " + e.getMessage());
        }
    }

    private static void eliminarReserva() {
        if (controller.getReservas().isEmpty()) {
            System.out.println("No hay reservas para eliminar.");
            return;
        }
        try {
            System.out.println("Ingrese la ID de la reserva que desea eliminar:");
            for (int i = 0; i < controller.getReservas().size(); i++) {
                System.out.println((i + 1) + ". " + controller.getReservas().get(i));
            }
            int choiceReserva = scanner.nextInt();
            scanner.nextLine();  // Consumir el resto de la línea

            controller.eliminarReserva(choiceReserva);
            System.out.println("Reserva eliminada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar la reserva: " + e.getMessage());
        }
    }

    private static void mostrarReservas() {
        if (controller.getReservas().isEmpty()) {
            System.out.println("No hay reservas.");
            return;
        }
        for (Reserva reserva : controller.getReservas()) {
            System.out.println(reserva);
        }
    }
}
