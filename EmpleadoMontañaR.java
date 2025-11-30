
public class EmpleadoMontañaR implements Runnable {

    ParqueDiversiones parque;

    public EmpleadoMontañaR(ParqueDiversiones p) {

        this.parque = p;
    }

    public void run() {

        try {
            while (!parque.getEstado() ) { //Mientras esté cerrado..

                Thread.sleep(1000);

            }

            while (parque.getCierre()) { //Mientras esté abierto....
                parque.iniciarMontañaRusa();
                Thread.sleep(1500);

            }




            
            //PUse un O en el caso de que el local cierre pero quede gente en la montaña, en ese caso haría ul último viaje para liberar gente.




            System.out.println("El empleado de la montaña rusa se va porque cerró el parque.");

        } catch (InterruptedException e) {
        }

    }

}

