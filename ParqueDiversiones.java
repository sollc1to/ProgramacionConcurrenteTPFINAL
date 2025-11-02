

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


    private int cantidadGente = 0, ocupantesMontaña = 0;

    public void actualizarHora(LocalTime horaA) {
        //Utilizo un semaforo para abrir y cerrar el comercio según la hora.

        this.hora = horaA;

        horaAux = hora.getHour();

        if (horaAux == 9) {
            System.out.println("Hora actual: " + hora.toString());
            abrirComercio.release();

        }

        if (horaAux == 18) {

            cerrarComercio.release();

        }

        if(horaAux == 19 ){ //Deberían cerrar todas las atracciones.

            try {
                  esperaMontaña.acquire(5); 





                
            } catch (InterruptedException e) {
            }

          



        }
        // Si hora == 9, abren los comercios.
        // Si hora == 18, se cierra el parque.
        // Si hora == 19, no puede entrar mas gente a las actividades.
        // Si hora == 23, no debería haber nadie.
    }
    private Semaphore abrirComercio = new Semaphore(0); // Tiene un permiso para abrir el comercio

    public void abrirComercio() { // Un empleado abrirá y cerrará los distintos comercios.
        try {
            // Se abre una vez q son las 9 .

            abrirComercio.acquire();
            System.out.println("El parque de diversiones abre :) pueden ingresar visitantes.");
            cantidadMolinetes.release(1); // Depende la cantidad de molinetes que haya es la cantidad q va en el release.

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private Semaphore cerrarComercio = new Semaphore(0);

    public void cerrarComercio() {

        try {
            cerrarComercio.acquire();

            System.out.println("El comercio cierra sus puertas a los clientes.");

            cantidadMolinetes.acquire();

        } catch (InterruptedException e) {
            // TODO: handle exception
        }

    }
    private Semaphore molineteDisponible = new Semaphore(0);

    public void ingresarParque() {
        //EL ingreso al parque se controla con un semáforo (que serían molinetes)
        //La cantidad de molinetes varía

        try {
            cantidadMolinetes.acquire();
            System.out.println("El visitante  " + Thread.currentThread().getName() + " ingreso al parque.");

            cantidadMolinetes.release();

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //MONTAÑA RUSA. La atracción de montaña rusa tiene capacidad para 5 personas.
    //Debe estar llena para iniciar.
    //Existe un espacio de espera: Sí este se llena el visitante se va a otro lado.
    //La cantidad de el espacio de espera puede variar.



    private Semaphore esperaMontaña = new Semaphore(5);

    public void esperarMontañaRusa(){
        //Utilizo un semáforo para realizar la espera de la montaña rusa.
        //Si pasan mas de 5 segundos y no pudo ingresar, se va a otro lado.

        try {
            if(esperaMontaña.tryAcquire(1000, TimeUnit.MILLISECONDS)){
                System.out.println("El visitante " + Thread.currentThread().getName() + " ingresó a la sala de espera de la montaña rusa.");

                subirMontañaRusa();
                


        }else{

            if(horaAux < 19){
                            System.out.println("El visitante " + Thread.currentThread().getName() + " se va a otra atracción, la sala de espera está llena.");

 
            }else{

                System.out.println("El visitante se va de la montaña rusa, los comercios");



            }

        }





            
        } catch (InterruptedException e) {
        }


    }

    private Semaphore mutex = new Semaphore(5);


    public void subirMontañaRusa() {

        try {
            mutex.acquire(); //Una vez que sube
            esperaMontaña.release(); //Libera el lugar de la sala de espera.
                System.out.println( "El visitante " + Thread.currentThread().getName() + " Se sentó en la montaña Rusa :)");
                ocupantesMontaña++;
                cantidadGente++; //Esto es para tener un contador de toda la gente en el parque.


                System.out.println("Ocupantes: " + ocupantesMontaña + " /5");

                sillasMontaña.release();



            bajarMontañaRusa(); //Preguntar si está bien poner estos metodos dentro de otros metodos.

        
        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void bajarMontañaRusa(){

        try {

                bajarMontaña.acquire();
                System.out.println("El visitante " + Thread.currentThread().getName() + " se baja de la montaña.");



                mutex.release(); //UNa vez que baja, puede subir otra persona de la sala de espera.




        } catch (Exception e) {
        }


    }

    public void iniciarMontañaRusa() { //Tengo que ver como hacer que el hilo de la montaña rusa muera una vez q cierra el comercio, auqnue no debería creo.


        try {
            sillasMontaña.acquire(5);

            System.out.println("La montaña rusa está llena.. arranca");

            Thread.sleep(500);

            System.out.println("La montaña rusa terminó su recorrido");

            ocupantesMontaña = 0;
            cantidadGente = cantidadGente - 5;

            bajarMontaña.release(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }



//Los autos chocadores son en total 10 autos, y cada uno requiere dos personas.
//Comienza solo cuándo todos los autos están ocupados.


    private Semaphore subirAuto = new Semaphore(20), encenderAuto = new Semaphore(0);
    private Semaphore bajarAuto = new Semaphore(0);

    public void subirAutoChocador() {

        try {


//Debería ver la hora de cierre antes.


            if (subirAuto.tryAcquire(5,TimeUnit.SECONDS)){ //Si no entra en 5 segundos. Esta bien usar este?

            System.out.println("El visitante " + Thread.currentThread().getName() + " Se subió a un auto chocador");

            encenderAuto.release(); // Deben liberar 20 permisos




            }

           

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

    public void subirBarcoPirata() { // Como no dice que ingresan por orden de llegada, utilizo un lock. 
        //Debería verificar la hora.
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
