

public class EmpleadoFicha implements Runnable{

    private ParqueDiversiones parque;


    public EmpleadoFicha (ParqueDiversiones p){
        parque = p;
    }


    public void run(){

        while(true){



            parque.cambiarFichaE();
        }






    }


    
}
