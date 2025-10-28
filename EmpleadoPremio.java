
package com.mycompany.tpfinalconcurrente;

public class EmpleadoPremio implements Runnable {

    private ParqueDiversiones parque;

    public EmpleadoPremio(ParqueDiversiones p) {
        parque = p;
    }

    public void run() {

        while (true) {

            parque.cambiarPremioE();
        }

    }

}
