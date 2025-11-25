/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class Visitante implements Runnable {

    private ParqueDiversiones parque;
    private Exchanger<String> cambiarFicha;

    public Visitante(ParqueDiversiones parque) {
        this.parque = parque;

    }

    public void run() {
        int i = 0;

        try {





            while(!parque.getEstado()){

                if (i==0){ //Esto es para que solo lo miprima una vez.
                    System.out.println(Thread.currentThread().getName() + " no puede ingresar porque el parque está cerrado.");
                    i =1;

                }

                 Thread.sleep(3400);

            }

            parque.ingresarParque();


            while (true) { 

                   parque.subirTren();
                
            }

         

            /* 
            parque.subirBarcoPirata();

            Thread.sleep(1000);
            

            parque.bajarBarcoPirata();


                Thread.sleep(1000);

      */

/* 
                 Thread.sleep(3400);

            
            parque.esperarMontañaRusa();  

       
                 Thread.sleep(3400);

            parque.subirAutoChocador();

        
            */

           

            
         
            /* 
            
            Thread.sleep(500);

            parque.subirAutoChocador();

            parque.bajarAutoChocador();

            Thread.sleep(500);

            parque.subirBarcoPirata();
            parque.bajarBarcoPirata();


            Thread.sleep(500);


            parque.cambiarFichaV();

            System.out.println("El hilo " + Thread.currentThread().getName() + " esta jugando");
            Thread.sleep(500);

            parque.cambiarFichaJugar();

            System.out.println("El hilo " + Thread.currentThread().getName() + " se va del parque");


            Thread.sleep(500);





        parque.ingresarComedor(); */

            

        } catch (Exception err) {
        }

    }
}
