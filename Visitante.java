/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Random;
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

        Random r = new Random();

        try {
            int i = 0;

            while (true) { // Se repite infinitamente.

                while (!parque.getEstado()) {
                    if (i == 0) {
                        System.out.println(Thread.currentThread().getName() +
                                " no puede ingresar porque el parque está cerrado.");
                        i = 1;
                    }
                    Thread.sleep(3000);
                }

                parque.ingresarParque(); // INgresa al parque de atracciones.
                System.out.println(Thread.currentThread().getName() + " ingresó al parque.");

                while (parque.getEstado()) {

                    int opcion = r.nextInt(8); // número entre 0 y 7

                    switch (opcion) {
                        case 0:
                            parque.subirAutoChocador();
                            Thread.sleep(1000);

                            break;
                        case 1:
                            parque.subirBarcoPirata();
                            Thread.sleep(1000);

                            break;
                        case 2:
                            parque.cambiarFichaV();
                            Thread.sleep(1000);

                            break;
                        case 3:
                            parque.ingresarComedor();
                            Thread.sleep(1000);

                            break;
                        case 4:
                            parque.subirTren();
                            Thread.sleep(1000);

                            break;
                        case 5:
                            parque.ingresarVR();
                            Thread.sleep(1000);

                            break;

                        case 6:

                            System.out.println(Thread.currentThread().getName() + " decidió ir al shopping.");
                            Thread.sleep(1000);
                            break;

                        case 7:
                            parque.ingresarComedor();
                            Thread.sleep(1000);

                            break;

                        default:
                            Thread.sleep(1000);
                            break;

                    }

                    Thread.sleep(1500); // pausa entre atracciones
                }

                System.out.println(Thread.currentThread().getName() + " se va del parque porque  cerró.");

            }

        } catch (InterruptedException e) {
        }

    }
}
