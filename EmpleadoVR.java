





public class EmpleadoVR implements Runnable{

    private ParqueDiversiones parque;

    public EmpleadoVR(ParqueDiversiones parque) {
        this.parque = parque;
    }

    public void run() {



        while(!parque.getEstado()){ //Espera a que el parque abra..

        }

        while (parque.getEstado()) {

            try {
                parque.darEquipo();
                Thread.sleep(1000);
                

            } catch (InterruptedException e) {
            }

        }

        parque.cerrarLocalVR();

    }

}

    

