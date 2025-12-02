
public class EmpleadoAutoChocador implements Runnable {

    private ParqueDiversiones parque;

    public EmpleadoAutoChocador(ParqueDiversiones parque) {
        this.parque = parque;
    }

    public void run() {

        try {

            while (true) {
                
                
            while (!parque.getCierre()) {
                Thread.sleep(1500);

            }

            System.out.println("El empleado de los autos chocadores abre el local ");

            while (parque.getCierre()) {

                parque.encenderAutoC();
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
