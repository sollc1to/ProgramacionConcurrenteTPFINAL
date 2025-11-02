/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.time.LocalTime;
import static java.time.temporal.TemporalQueries.localTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class Reloj implements Runnable {

    //Este hilo se encargará de ir actualizando la hora actual del día.
    private LocalTime hora = LocalTime.of(00, 00, 00); //Empieza a las 00.
    private int horaAux = 0;
    private ParqueDiversiones parque;

    public Reloj(ParqueDiversiones parque) {
        this.parque = parque;
    }

    public void run() {

        try {

            while (true) {

                hora = LocalTime.of(horaAux, 00, (int) (Math.random() * 60));
                System.out.println("Hora actual: " + hora);

                parque.actualizarHora(hora);

                Thread.sleep((int) (Math.random() * 2500 + 1000));

                horaAux++;

                if (horaAux == 24) {
                    horaAux = 0;

                }
                //metodo parquediversiones actualizar hora. 

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(Reloj.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
