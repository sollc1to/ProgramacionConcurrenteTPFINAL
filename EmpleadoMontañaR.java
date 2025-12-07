
public class EmpleadoMontañaR implements Runnable {

    private ParqueDiversiones parque;
        private AtraccionesMecanicas atrc;


    public EmpleadoMontañaR(ParqueDiversiones p, AtraccionesMecanicas atrc) {
        this.atrc = atrc;
        this.parque = p;
    }

    public void run() {

        try {
            

                while (true) {

                    while (!parque.getEstado()) { //Mientras esté cerrado..

                        Thread.sleep(1000);

                    }

                    while (parque.getCierre()) { //Mientras esté abierto....
                        atrc.iniciarMontañaRusa();
                        Thread.sleep(1500);

                    }

                    //PUse un O en el caso de que el local cierre pero quede gente en la montaña, en ese caso haría ul último viaje para liberar gente.
                    System.out.println("El empleado de la montaña rusa se va porque cerró el parque.");

                    Thread.sleep(10000);

                }

            }catch (InterruptedException e) {
        }

        }

    }
