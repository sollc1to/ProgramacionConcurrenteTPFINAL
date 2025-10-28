/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tpfinalconcurrente;

import java.util.concurrent.Exchanger;
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

        try {

         
            parque.ingresarParque();    
            

            /*
            parque.subirMontañaRusa();

            Thread.sleep(500);

            parque.subirAutoChocador();

            parque.bajarAutoChocador();

            Thread.sleep(500);

            parque.subirBarcoPirata();
            parque.bajarBarcoPirata();


            Thread.sleep(500);*/


            parque.cambiarFichaV();

            System.out.println("El hilo " + Thread.currentThread().getName() + " esta jugando");
            Thread.sleep(500);

            parque.cambiarFichaJugar();

            System.out.println("El hilo " + Thread.currentThread().getName() + " se va del parque");


            Thread.sleep(500);





        parque.ingresarComedor();

            

        } catch (Exception err) {
        }

    }
}
