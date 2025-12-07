

public class EmpleadoPremio implements Runnable {

    private ParqueDiversiones parque;
        private Actividades intercambio;


    public EmpleadoPremio(ParqueDiversiones p, Actividades a) {
        this.intercambio = a;
        parque = p;
    }

    public void run() {



            try {

                while (true) { 


                while(!parque.getEstado()){

                }
                while(parque.getCierre()){

                intercambio.cambiarFichaE();
                Thread.sleep(1000);




                }

                System.out.println("El empleado de la atracción de intercambios se va a su casa.");
                Thread.sleep(10000);
                    
                }

           
            } catch (InterruptedException e) {
            }
           
        

    }

}
