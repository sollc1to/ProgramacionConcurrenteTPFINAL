
import java.time.LocalTime;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParqueDiversiones {
    // HAcer diagrama de como puede moverse el pasajero

    private int horaAux;
   private AtomicInteger cantidadMoli;
    private AtomicInteger cantidadMLibre;




    private AtomicBoolean estado = new AtomicBoolean(false); // Creo un atomicboolean para manejar la apertura y cierre.
    private AtomicBoolean cierre = new AtomicBoolean(false);

    private boolean salir = false;

    // Utilizo este como una bandera. Está el estado abierto, cerrado, cerrando.

    public ParqueDiversiones(int molinetes) {

        this.cantidadMoli = new AtomicInteger(molinetes);
        this.cantidadMLibre = new AtomicInteger(0);

    }

    public void actualizarHora(LocalTime horaA) {
        // Utilizo un semaforo para abrir y cerrar el comercio según la hora.

        horaAux = horaA.getHour();

        // Si hora == 9, abren los comercios.
        // Si hora == 18, se cierra el parque.
        // Si hora == 19, no puede entrar mas gente a las actividades.
        // Si hora == 23, no debería haber nadie.
    }

    public synchronized  void abrirComercio() { // Un empleado abrirá y cerrará los distintos comercios.

        try {
            // Se abre una vez q son las 9 .
            while (horaAux < 9 || horaAux >= 18) {
                wait(1000); //Espera un tiempo

            }

            System.out.println(ANSI_Colors.GREEN + "El parque de diversiones abre :) pueden ingresar visitantes."+ ANSI_Colors.RESET);



            cierre.compareAndExchange(false, true);
            estado.compareAndExchange(false, true);
            
            salir = false;


            cantidadMLibre.set(cantidadMoli.get()); //Ponemos como libre a la cantidad de molinetes actuales.

            notifyAll();


        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public synchronized void cerrarIngreso() {

        try {

            while (horaAux < 18) {
                wait(1000);
            }

            System.out.println(ANSI_Colors.RED + "El comercio cierra sus puertas a los clientes." + ANSI_Colors.RESET);
            
            cantidadMLibre.set(0); //No hay ningun molinete libre.



            estado.compareAndExchange(true, false);
            


        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    public synchronized  void cerrarParque() {

        try {

            while (horaAux < 21) {
                wait(1000);
            }

            System.out.println(ANSI_Colors.RED + "El comercio cierra sus puertas a los clientes y empleados." + ANSI_Colors.RESET);
        
            salir = true; //Para que se vayan.

            cierre.compareAndExchange(true, false);

            notifyAll();


        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }
    //EL ingreso lo hago en dos partes: compra entrada y luego entra.

    public synchronized void comprarEntrada() {
   

    try {
        // Espera guardada:
        // Mientras NO se haya dado la orden de salir
        // y (el ingreso esté cerrado o no haya molinetes libres)
        while (salir && (!estado.get() || cantidadMLibre.get() == 0)) {
            System.out.println(Thread.currentThread().getName() + " está esperando para comprar entrada / usar molinete.");
            wait();  // queda en el conjunto de espera del monitor
        }

        // Si se dio la orden de salir o el ingreso está cerrado, se va sin comprar
        if (!cierre.get()) {
            System.out.println(Thread.currentThread().getName() + " se va porque el comercio está cerrado para ingreso.");
           
        }else{


        cantidadMLibre.decrementAndGet();  // ocupa un molinete
        System.out.println(Thread.currentThread().getName() + " compró la entrada y está pasando por el molinete.");
        ingresarParque();

        // acá todavía no liberamos el molinete
        // se libera en ingresarParque()




        }
            

      
    } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
    }
}


    public synchronized  void ingresarParque() {
      

            if(cierre.get()){ //Se fija si esta cerrado definitivamente el local, esto es para que se vayan.
                System.out.println(Thread.currentThread().getName() + " ingresó al parque.");

                notify();


            }else{


                System.out.println(Thread.currentThread().getName() + " Se va del parque porque cerró el comercio.");



            }

          


        // return estado.get() esto estará mal???. POdría hacer que vea el estado y
        // después ingresa.

    }

    public boolean getEstado() {

        return estado.get();
    }

    public boolean getCierre() {

        return cierre.get();
    }

}
