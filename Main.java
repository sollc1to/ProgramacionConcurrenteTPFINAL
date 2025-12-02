import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ParqueDiversiones parque = new ParqueDiversiones(1, 2);

        Thread reloj = new Thread(new Reloj(parque));
        reloj.start();
        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();

        Scanner sc = new Scanner(System.in);

        Thread barcoP = new Thread(new EmpleadoBarcoPirata(parque)); // 1
        Thread montana = new Thread(new EmpleadoMontañaR(parque)); // 2
        Thread empleadoPremio = new Thread(new EmpleadoPremio(parque)); // 3
        Thread empleadoTren = new Thread(new Empleadotren(parque)); // 4
        Thread empleadoVR = new Thread(new EmpleadoVR(parque)); // 5
        Thread empleadoAuto = new Thread(new EmpleadoAutoChocador(parque)); // 6

        boolean salir = false;

        System.out.println("\n===== MENU DE ACTIVIDADES =====");
        System.out.println("1. Solo Barco Pirata");
        System.out.println("2. Solo Montaña Rusa");
        System.out.println("3. Solo Juegos de Premio");
        System.out.println("4. Solo Tren");
        System.out.println("5. Solo Realidad Virtual");
        System.out.println("6. Solo Autos Chocadores");
        System.out.println("7. TODAS las actividades en conjunto");
        System.out.println("Q. Cerrar parque y salir del programa");
        System.out.print("Elegí una opción: ");

        String opcionStr = sc.next().toUpperCase();

        while (!salir) {

            switch (opcionStr) {
                case "1":
                    barcoP.start();
                    crearVisitantes(parque, 1);
                    break;

                case "2":
                    montana.start();
                    crearVisitantes(parque, 2);
                    break;

                case "3":
                    empleadoPremio.start();
                    crearVisitantes(parque, 3);
                    break;

                case "4":
                    empleadoTren.start();
                    crearVisitantes(parque, 4);
                    break;

                case "5":
                    empleadoVR.start();
                    crearVisitantes(parque, 5);
                    break;

                case "6":
                    empleadoAuto.start();
                    crearVisitantes(parque, 6);
                    break;

                case "7":
                    barcoP.start();
                    montana.start();
                    empleadoPremio.start();
                    empleadoTren.start();
                    empleadoVR.start();
                    empleadoAuto.start();
                    crearVisitantes(parque, 7); // 7 = pueden usar todas las atracciones
                    break;

                case "Q":
                    System.out.println("Cerrando parque y finalizando simulación...");
                    parque.cerrarParque(); // este método lo hacés vos: estado = false, etc.
                    sc.close();
                    System.exit(0); // mata todos los hilos y termina el programa
                    break;

                default:
                    System.out.println("Opción inválida.");
                    break;
            }

            opcionStr = sc.next().toUpperCase();
        }

        sc.close();
    }

    private static void crearVisitantes(ParqueDiversiones parque, int opcionActividad) {
        String[] nombres = {
                "Lucas", "María", "Sofía", "Juan", "Lautaro",
                "Camila", "Mateo", "Julieta", "Thiago", "Valentina",
                "Bruno", "Martina", "Benjamín", "Luciana", "Agustín",
                "Emma", "Santiago", "Isabella", "Franco", "Renata",
                "Tomás", "Mía", "Bautista", "Morena", "Felipe",
                "Zoe", "Joaquín", "Catalina", "Simón", "Abril",
                "Ramiro", "Elena", "Nicolás", "Guadalupe", "Gael",
                "Luna", "Axel"
        };

        for (int i = 0; i < nombres.length; i++) { // Creamos y ponemos en start() a los visitantes con la opción
                                                   // ingresada.

            try {
                Thread visitante = new Thread(new Visitante(parque, opcionActividad), nombres[i]);
                visitante.start();

                visitante.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
