
public class EmpleadoBarcoPirata implements Runnable {

    private ParqueDiversiones parque;

    public EmpleadoBarcoPirata(ParqueDiversiones parque) {
        this.parque = parque;
    }

    public void run() {
        int i = 0;

        try {

            while (true) {

                while (!parque.getCierre()) { // MIentras el parque esté cerrado..

                }
                System.out.println("El empleado del barco pirata llega al parque de diversiones.");

                while (parque.getCierre()) {

                    parque.iniciarBarcoPirata();

                    Thread.sleep(7000);
                    parque.terminarBarcoPirata();

                }

                System.out.println("El empleado del barco pirata se va a su casa.");
                Thread.sleep(10000);

            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        }

    }

}
