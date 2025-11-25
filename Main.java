
public class Main {
    public static void main(String[] args) {

        ParqueDiversiones parque = new ParqueDiversiones();

    
     /*     Thread barcoP = new Thread(new BarcoPirata(parque));
        
         barcoP.start();

         
*/
                  
         Thread reloj = new Thread(new Reloj(parque));
         reloj.start();

        Thread montana = new Thread(new MontañaRusa(parque));

        montana.start();

        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();
    /* 
        Thread empleadoPremio = new Thread(new EmpleadoPremio(parque));
        empleadoPremio.start();


        */

        Thread empleadoTren = new Thread(new Empleadotren(parque));
        empleadoTren.start();




String[] nombres = {
    "Lucas", "María", "Sofía", "Juan", "Lautaro"
};

        for (int i = 0; i < nombres.length; i++) {
            try {
                Thread visitante = new Thread(new Visitante(parque), nombres[i]);
                visitante.start();

                visitante.sleep(400);


            
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }

    }

}
