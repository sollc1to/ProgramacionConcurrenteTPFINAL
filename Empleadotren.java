
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
    ParqueDiversiones parque;

    public Empleadotren(ParqueDiversiones parque) {
        this.parque = parque;
    }

    public void run() {

        while (true) {

            while (!parque.getEstado()) { // Espera a que el parque abra..

            }

            while (parque.getCierre()) {

                try {
                    parque.encenderTren();
                    Thread.sleep(10000);

                    parque.bajarPasajeros();
                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                }

            }

            System.out.println("El empleado del tren se va a su casa.");

        }

    }

}
