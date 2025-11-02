
public class MontañaRusa implements Runnable{

        ParqueDiversiones parque;


        public MontañaRusa (ParqueDiversiones p){

            this.parque = p;
        }


        public void run(){


            while(true){

                try {

                    parque.iniciarMontañaRusa();


                    Thread.sleep(500);
                    
                } catch (Exception e) {
                    // TODO: handle exception
                }
    
            }
            
        }




    






}
