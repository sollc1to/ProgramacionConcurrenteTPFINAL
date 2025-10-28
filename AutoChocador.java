package com.mycompany.tpfinalconcurrente;

public class AutoChocador implements Runnable {

    private ParqueDiversiones parque;


    public AutoChocador(ParqueDiversiones parque){
        this.parque = parque;
    }



    public void run(){



        try {


            while(true){


                parque.encenderAutoC();
                parque.detenerAutoC();




            }





            
        } catch (Exception e) {
            // TODO: handle exception
        }






    }





    
}
