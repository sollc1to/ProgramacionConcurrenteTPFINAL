
public class Main {
    public static void main(String[] args) {

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

        ParqueDiversiones parque = new ParqueDiversiones(1, 2);

        Thread reloj = new Thread(new Reloj(parque));
        reloj.start();
        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();

        Thread barcoP = new Thread(new EmpleadoBarcoPirata(parque)); // Opcion 1
        barcoP.start();

        Thread montana = new Thread(new EmpleadoMontañaR(parque)); //Opcion 2

        montana.start();


        Thread empleadoPremio = new Thread(new EmpleadoPremio(parque)); //Opcion 3
        empleadoPremio.start();

        Thread empleadoTren = new Thread(new Empleadotren(parque)); //Opcion 4
        empleadoTren.start();

        Thread empleadoVR = new Thread(new EmpleadoVR(parque)); //OPcion 5
        empleadoVR.start();

        Thread empleadoAuto = new Thread(new EmpleadoAutoChocador(parque)); //Opcion 6
        empleadoAuto.start();

        for (int i = 0; i < nombres.length; i++) {
            try {
                Thread visitante = new Thread(new Visitante(parque), nombres[i]);
                visitante.start();

                visitante.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
