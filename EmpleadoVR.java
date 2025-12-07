
public class EmpleadoVR implements Runnable {

    private ParqueDiversiones parque;
    private Actividades act;

    public EmpleadoVR(ParqueDiversiones parque, Actividades act) {
        this.parque = parque;

        this.act = act;

    }

    public void run() {

        while (true) {

            while (!parque.getEstado()) { // Espera a que el parque abra..

            }

            while (parque.getEstado()) {

                try {

                    act.darEquipo();
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                }

            }

            System.out.println("El empleado de VR se va a su casa.");

            act.cerrarLocalVR();

        }

    }

}
