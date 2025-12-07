/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Random;

public class Visitante implements Runnable {

    private ParqueDiversiones parque;
    private Actividades act; // 1 a 6
    private AtraccionesMecanicas atracc;

    public Visitante(ParqueDiversiones parque,Actividades act, AtraccionesMecanicas atracc) {
        this.parque = parque;
        this.act = act;
        this.atracc = atracc;
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

                parque.comprarEntrada(); // Ingresa al parque de atracciones.
                

                while(parque.getEstado()){

                    elegirActividad();


                }
            
                System.out.println(Thread.currentThread().getName() + " se va del parque porque  cerró.");

            }

        } catch (InterruptedException e) {
        }

    }


    public void elegirActividad() {

    int opcion = (int)(Math.random() * 7);  // 7 actividades → índices 0 a 6

    switch (opcion) {

        case 0:
            act.ingresarVR();
            break;

        case 1:
            act.subirTren();
            break;

        case 2:
            act.ingresarComedor();
            break;

        case 3:
            act.cambiarFichaV();
            break;

        case 4:
            atracc.subirBarcoPirata();
            break;

        case 5:
            atracc.subirAutoChocador();
            break;

        case 6:
            atracc.esperarMontañaRusa();
            break;

        default:
            // nunca pasa
            break;
    }
}

}