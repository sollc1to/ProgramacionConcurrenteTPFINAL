import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ParqueDiversiones parque = new ParqueDiversiones(1);

        Actividades act = new Actividades(parque, 5);

        AtraccionesMecanicas atracciones = new AtraccionesMecanicas(parque);
        
 
        Thread reloj = new Thread(new Reloj(parque));
        reloj.start();
        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();


         Thread empleadoTren = new Thread(new Empleadotren(parque,act));

         empleadoTren.start();
        
      

        Thread barcoP = new Thread(new EmpleadoBarcoPirata(parque, atracciones)); 
          barcoP.start();
        Thread montana = new Thread(new EmpleadoMontañaR(parque,atracciones));  
        montana.start();
        Thread empleadoPremio = new Thread(new EmpleadoPremio(parque, act));  
        empleadoPremio.start();
        Thread empleadoVR = new Thread(new EmpleadoVR(parque, act)); 
        empleadoVR.start();
        Thread empleadoAuto = new Thread(new EmpleadoAutoChocador(parque,atracciones)); 
        empleadoAuto.start();

      








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
                Thread visitante = new Thread(new Visitante(parque, act,atracciones), nombres[i]);
                visitante.start();

                visitante.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


            

    } 
/*
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
                Thread visitante = new Thread(new Visitante(parque, act), nombres[i]);
                visitante.start();

                visitante.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 */
}
