

public class EmpleadoPremio implements Runnable {

    private ParqueDiversiones parque;

    public EmpleadoPremio(ParqueDiversiones p) {
        parque = p;
    }

    public void run() {

        while (true) {


            try {
                
            parque.cambiarFichaE();
            Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
           
        }

    }

}
