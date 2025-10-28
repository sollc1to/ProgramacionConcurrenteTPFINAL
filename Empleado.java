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
public class Empleado implements Runnable{
    ParqueDiversiones parque;
    Exchanger <String> cambiarPremio;
    
    public Empleado(ParqueDiversiones parque){
        this.parque  = parque;
    }
    
    public void run(){


        while(true){
            
        parque.abrirComercio();
        parque.cerrarComercio();

        }

   
        
      
    }
    
}
