/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.TemporalQueries.localTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class Reloj implements Runnable {

    //Este hilo se encargará de ir actualizando la hora actual del día.
    private  LocalTime hora = LocalTime.of(00, 00, 00); //Empieza a las 00.
    private int horaAux = 8, minutoAux = 0;
    private ParqueDiversiones parque;

    public Reloj(ParqueDiversiones parque) {
        this.parque = parque;
    }

 
public void run() {
    try {
        while (true) {

            // Hora simulada: horaAux:00
            hora = LocalTime.of(horaAux, 0);

            // Formateo bonito HH:mm
            String horaFormateada = hora.format(DateTimeFormatter.ofPattern("HH:mm"));

            // Imprimir solo una vez por HORA con un formato sencillo
            System.out.println(
                    ANSI_Colors.YELLOW + "[RELOJ] " +
                    ANSI_Colors.CYAN + horaFormateada +
                    ANSI_Colors.RESET
            );

            // Avisar al parque la nueva hora
            parque.actualizarHora(hora);

            // Esperar entre 4 y 6 segundos antes de avanzar otra hora
            Thread.sleep((int) (Math.random() * 2000 + 4000));
            // 4000–6000 ms → el tiempo no pasa TAN rápido

            // Avanzar la hora simulada
            horaAux++;
            if (horaAux == 24) {
                horaAux = 0;
            }
        }

    } catch (InterruptedException ex) {
        Logger.getLogger(Reloj.class.getName()).log(Level.SEVERE, null, ex);
        // Podés cortar el while si querés terminar el hilo:
        // Thread.currentThread().interrupt();
    }
}




}
