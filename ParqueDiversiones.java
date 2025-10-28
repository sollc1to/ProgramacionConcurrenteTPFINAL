
package com.mycompany.tpfinalconcurrente;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParqueDiversiones {

    private LocalTime hora = LocalTime.of(00, 00, 00);
    private int horaAux, cantidadMoli;
    private Semaphore cantidadMolinetes = new Semaphore(0), sillasMontaña = new Semaphore(0);
    private Semaphore iniciarMontana = new Semaphore(0), bajarMontaña = new Semaphore(0); // Obtiene un permiso cuándo
                                                                                          // se llena
    private Semaphore mutex = new Semaphore(1); // Para verificar si está lleno o no

    private Semaphore cerrarComercio = new Semaphore(0);

    private Semaphore abrirComercio = new Semaphore(0); // Tiene un permiso para abrir el comercio
    private int cantidadGente = 0, ocupantesMontaña = 0;
    private Semaphore molineteDisponible = new Semaphore(0);

    public void actualizarHora(LocalTime horaA) {

        this.hora = horaA;

        horaAux = hora.getHour();

        if (horaAux == 9) {
            System.out.println("Hora actual: " + hora.toString());
            abrirComercio.release();

        }

        if (horaAux == 19) {

            cerrarComercio.release();

        }
        // Si hora == 9, abren los comercios.
        // Si hora == 18, se cierra el parque.
        // Si hora == 19, no puede entrar mas gente a las actividades.
        // Si hora == 23, no debería haber nadie.
    }

    public void abrirComercio() { // Un empleado abrirá y cerrará los distintos comercios.
        try {
            // Se abre una vez q son las 9 .

            abrirComercio.acquire();
            System.out.println("El parque de diversiones abre :) pueden ingresar visitantes.");
            cantidadMolinetes.release(1); // Pongo uno de prueba aunq crep que funciona

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cerrarComercio() {

        try {
            cerrarComercio.acquire();

            System.out.println("El comercio cierra sus puertas a los clientes.");

            cantidadMolinetes.acquire();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void ingresarParque() {

        try {
            cantidadMolinetes.acquire();
            System.out.println("El visitante  " + Thread.currentThread().getName() + " ingreso al parque.");

            cantidadMolinetes.release();

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void subirMontañaRusa() {

        try {
            mutex.acquire();

            if (ocupantesMontaña <= 4) {

                System.out.println(
                        "El visitante " + Thread.currentThread().getName() + " Se sentó en la montaña Rusa :)");
                sillasMontaña.release();

                ocupantesMontaña++;
                System.out.println("Ocupantes: " + ocupantesMontaña + " /5");

                mutex.release();

                Thread.sleep(500);

                bajarMontaña.acquire();
                System.out.println("El visitante " + Thread.currentThread().getName() + " se baja de la montaña.");

            } else {
                // Tener en cuenta la hora. Si son dps de las 19, se tiene que ir

                System.out.println("El visitante " + Thread.currentThread().getName()
                        + " se va a otra atracción ya que la montaña esta llena.");
                mutex.release();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void iniciarMontañaRusa() {

        try {
            sillasMontaña.acquire(5);

            System.out.println("La montaña rusa está llena.. arranca");

            Thread.sleep(100);

            System.out.println("La montaña rusa terminó su reocrrido");

            ocupantesMontaña = 0;

            bajarMontaña.release(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    private Semaphore subirAuto = new Semaphore(20), encenderAuto = new Semaphore(0);
    private Semaphore bajarAuto = new Semaphore(0);

    public void subirAutoChocador() {

        try {

            subirAuto.acquire();

            System.out.println("El visitante " + Thread.currentThread().getName() + " Se subió a un auto chocador");

            encenderAuto.release(); // Deben liberar 20 permisos

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void bajarAutoChocador() {

        try {

            bajarAuto.acquire();

            System.out.println("El visitante " + Thread.currentThread().getName()
                    + " se baja de la atracción autos chocadores :)\n");
            subirAuto.release(); // Por cada uno que se baja, se puede subir otro

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void encenderAutoC() {

        try {

            encenderAuto.acquire(20);

            System.out.println("La atracción de autos chocadores inició..");

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void detenerAutoC() {

        try {

            System.out.println("La atracción de autos chocadores se detiene.");
            bajarAuto.release(20);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /*
     * c. El barco pirata tiene capacidad para 20 personas. El viaje comienza cuando
     * todas las plazas están llenas, o bien después de un determinado tiempo de
     * espera. Los visitantes que no logran ingresar deben esperar al próximo viaje.
     */
    private Lock barcoPirata = new ReentrantLock();
    private Condition barco = barcoPirata.newCondition();
    private Condition visitantes = barcoPirata.newCondition();
    private Condition barcoP = barcoPirata.newCondition();

    private int cantidadBarco = 0;
    private boolean bajar = false;

    public void subirBarcoPirata() { // Como no dice que ingresan por orden de llegada, utilizo un lock
        barcoPirata.lock();
        try {
            while (cantidadBarco == 20) {
                visitantes.await();
            }

            cantidadBarco++;
            System.out.println("El visitante " + Thread.currentThread().getName() + " ingresó al barco pirata");

            if (cantidadBarco == 20) {
                barcoP.signal();
            }

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void bajarBarcoPirata() { // Como no dice que ingresan por orden de llegada, utilizo un lock
        barcoPirata.lock();
        try {
            while (!bajar) { // Espera a que el barco termine
                barco.await();

            }

            cantidadBarco--;
            System.out.println("El visitante " + Thread.currentThread().getName() + " bajó del barco pirata");

            visitantes.signal();

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void iniciarBarcoPirata() {
        barcoPirata.lock();
        try {
            while (cantidadBarco != 20) {

                barcoP.await(7, TimeUnit.SECONDS); // Espera 7 segundos

            }

            System.out.println("El barco pirata inició :D");

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void terminarBarcoPirata() {
        barcoPirata.lock();
        try {

            System.out.println("El barco pirata terminó");

            barco.signalAll();

            bajar = true;

        } finally {
            barcoPirata.unlock();

        }

    }

    private CyclicBarrier mesa = new CyclicBarrier(4);

    public void ingresarComedor() { // Tendría que tener un recuento máxio de mesas

        try {
            System.out.println("El hilo " + Thread.currentThread().getName() + " ingresa al comedor");

            mesa.await();

            System.out.println("El hilo " + Thread.currentThread().getName() + " Ya está comiendo :D");

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private Exchanger<String> intercambioF = new Exchanger<>();
    private Exchanger<String> intercambioP = new Exchanger<>();

    // Debería haber dos encargados y dos exchanger

    public synchronized void cambiarFichaV() { //Agrego el synchronized para que no ingresen dos hilos visitante 
        //y se intercambien entre ellos

        String pase;

        try {
            pase = intercambioF.exchange("Dinero");

            System.out.println("El hilo " + Thread.currentThread().getName() + " obtuvo un " + pase);

            // El jugar sería un sleep

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public synchronized void cambiarFichaJugar() {
        String premio;
        try {
            premio = intercambioP.exchange("Ficha");

            System.out.println("El hilo " + Thread.currentThread().getName()
                    + " intercambió una ficha de juego para obtener un " + premio);

            // El jugar sería un sleep

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void cambiarFichaE(){

        String d;

        try {
            d = intercambioF.exchange("pase",2,TimeUnit.SECONDS);

            System.out.println("El encargado intercambió una ficha");

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void cambiarPremioE() {
        String f;

        try {
            f = intercambioP.exchange("premio " + generarPremio(),2,TimeUnit.SECONDS);

            System.out.println("El encargado intercambió un premio");

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private String generarPremio() {
        int puntos = (int) (Math.random() * 100); // puntaje
        if (puntos < 33)
            return "chico";
        else if (puntos < 66)
            return "mediano";
        else
            return "grande";
    }


/* Para recorrer el parque, los visitantes pueden abordar un tren turístico que recorre
las instalaciones. Los visitantes se colocan en una cola para esperar que el tren
llegue. El tren tiene capacidad para 10 personas y parte una vez que la fila está
llena o después de 5 minutos de espera, lo que ocurra primero.
 */
BlockingQueue<Thread> tren = new ArrayBlockingQueue<>(10);





/* public void subirTren(){


    try {

        tren.put(Thread.currentThread());


        System.out.println("El hilo " + Thread.currentThread().getName() + " se subió al tren. ");
        
    } catch (Exception e) {
        // TODO: handle exception
    }




public void bajarPasajeros(){


    try {






        tren.take();









        
    } catch (Exception e) {
        // TODO: handle exception
    }










}

 */









}
