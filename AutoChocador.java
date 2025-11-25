




public class AutoChocador implements Runnable {

    private ParqueDiversiones parque;


    public AutoChocador(ParqueDiversiones parque){
        this.parque = parque;
    }



    public void run(){



        try {


            while(true){


                parque.encenderAutoC();
                Thread.sleep(1000);

                parque.detenerAutoC();
            




            }





            
        } catch (Exception e) {
            // TODO: handle exception
        }






    }





    
}
