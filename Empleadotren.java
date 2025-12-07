
import java.io.InterruptedIOException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PC
 */
public class Empleadotren implements Runnable {
    private ParqueDiversiones parque;
    private Actividades tren;
    

    public Empleadotren(ParqueDiversiones parque, Actividades tren) {


        this.tren = tren;

        this.parque = parque;
    }

    public void run() {


        try {


        while (true) {

            while (!parque.getEstado()) { // Espera a que el parque abra..

            }

            while (parque.getCierre()) {


                tren.encenderTren();

                Thread.sleep(1000);
                tren.bajarPasajeros();
            }

            tren.bajarPasajerosT();

        
            System.out.println("El empleado del tren se va a su casa.");

        }
            
        } catch (InterruptedException e) {
        }


    }

}
