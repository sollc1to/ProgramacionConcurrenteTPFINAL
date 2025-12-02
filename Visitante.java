/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Random;

public class Visitante implements Runnable {

    private ParqueDiversiones parque;
    private int actividad; // 1 a 6

    public Visitante(ParqueDiversiones parque, int actividad) {
        this.parque = parque;
        this.actividad = actividad;
    }

    public void run() {
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

                parque.ingresarParque(); // Ingresa al parque de atracciones.
                System.out.println(Thread.currentThread().getName() + " ingresó al parque.");

                while (parque.getEstado()) {

                    if (actividad <= 6) { // En este caso solo ejecutamos la actividad individual.

                        ejecutarActividad();

                    } else {
                        ejecutarTodasActividades();
                    }
                    Thread.sleep(1500); // pausa entre atracciones
                }

                System.out.println(Thread.currentThread().getName() + " se va del parque porque  cerró.");

            }

        } catch (InterruptedException e) {
        }

    }

    private void ejecutarTodasActividades() {

        Random r = new Random();
        int act = r.nextInt(8); // número entre 0 y 7

        try {

            switch (act) {
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

        } catch (InterruptedException e) {
        }

    }

    private void ejecutarActividad() { // Este es para ejecutar un actividad individualmente.
        switch (this.actividad) {
            case 1:
                parque.subirBarcoPirata();
                break;
            case 2:
                parque.subirMontañaRusa(); // ajustá al nombre real del método
                break;
            case 3:
                parque.cambiarFichaV(); // si este es "premio"
                break;
            case 4:
                parque.subirTren();
                break;
            case 5:
                parque.ingresarVR();
                break;
            case 6:
                parque.subirAutoChocador();
                break;
        }
    }
}
