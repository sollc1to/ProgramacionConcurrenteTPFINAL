/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */





/**
 *
 * @author PC
 */
public class Empleado implements Runnable{
    ParqueDiversiones parque;
    
    public Empleado(ParqueDiversiones parque){
        this.parque  = parque;
    }
    
    public void run(){


        while(true){
            
        parque.abrirComercio();
        parque.cerrarIngreso();
        parque.cerrarParque();

        }

   
        
      
    }
    
}
