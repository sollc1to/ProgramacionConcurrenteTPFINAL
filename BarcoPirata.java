


public class BarcoPirata implements Runnable{

    private ParqueDiversiones parque;


    public BarcoPirata(ParqueDiversiones parque){
        this.parque = parque;
    }



    public void run(){



        try {


            while(true){


                parque.iniciarBarcoPirata();

                Thread.sleep(1000);
                parque.terminarBarcoPirata();




            }





            
        } catch (Exception e) {
            // TODO: handle exception
        }






    }





  

    
}
