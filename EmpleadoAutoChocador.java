
public class EmpleadoAutoChocador implements Runnable {

    private ParqueDiversiones parque;
    private AtraccionesMecanicas atrc;

    public EmpleadoAutoChocador(ParqueDiversiones parque, AtraccionesMecanicas atrc) {
        this.parque = parque;
        this. atrc =atrc;
    }

    public void run() {

        try {

            while (true) {
                
                
            while (!parque.getCierre()) {
                Thread.sleep(1500);

            }

            System.out.println("El empleado de los autos chocadores abre el local ");

            while (parque.getCierre()) {

                atrc.encenderAutoC();
                Thread.sleep(10000);

            }

   

            System.out.println("EL empleado de los autos se va a su casa.");

            Thread.sleep(10000);
                
            }


        } catch (InterruptedException e) {
            // TODO: handle exception
        }

    }

}
