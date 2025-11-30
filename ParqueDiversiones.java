
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParqueDiversiones {
    // CHEQUEAR QUE LAS ATRACCIONES ARRANQUEN CUANDO EL PARQUE ABRE.
    // ARREGLAR LAS INTERRUPCIONES Y UTILIZARLAS CORRECTAMENTE.
    // AGREGAR LOS PARÁMETROS QUE VARÍAN SEGUN LA ATRACCION
    // AGREGAR COLORES PARA QUE SEA MÁS LEGIBLE

    private int horaAux;
    private int cantidadMoli;
    private Semaphore cantidadMolinetes = new Semaphore(0);

    private AtomicBoolean estado = new AtomicBoolean(false); // Creo un atomicboolean para manejar la apertura y cierre.
    public AtomicBoolean cierre = new AtomicBoolean(false);

    // Utilizo este como una bandera. Está el estado abierto, cerrado, cerrando.

    private int ocupantesMontaña = 0;

    public ParqueDiversiones(int molinetes, int cantidadMesas) {

        this.cM = cantidadMesas;
        this.cantidadMoli = molinetes;

    }

    public boolean getCierre(){
        return cierre.get();
    }

    public void actualizarHora(LocalTime horaA) {
        // Utilizo un semaforo para abrir y cerrar el comercio según la hora.

        horaAux = horaA.getHour();

        // Si hora == 9, abren los comercios.
        // Si hora == 18, se cierra el parque.
        // Si hora == 19, no puede entrar mas gente a las actividades.
        // Si hora == 23, no debería haber nadie.
    }

    public void abrirComercio() { // Un empleado abrirá y cerrará los distintos comercios.
        try {
            // Se abre una vez q son las 9 .
            while (horaAux < 9 || horaAux >= 18) {
                Thread.sleep(1500);

            }

            System.out.println(ANSI_Colors.GREEN + "El parque de diversiones abre :) pueden ingresar visitantes."
                    + ANSI_Colors.RESET);

            cierre.compareAndExchange(false, true);
            estado.compareAndExchange(false, true);
            cantidadMolinetes.release(cantidadMoli);
            // Depende la cantidad de molinetes que haya es la cantidad q va en el
            // release.

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cerrarIngreso() {

        try {

            while (horaAux < 18) {
                Thread.sleep(1500);
            }

            System.out.println(ANSI_Colors.RED + "El comercio cierra sus puertas a los clientes." + ANSI_Colors.RESET);
            estado.compareAndExchange(true, false);

            cantidadMolinetes.acquire(cantidadMoli); // Ya no puede ingresar más gente.

        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    public void cerrarParque() {

        try {

            while (horaAux < 21) {
                Thread.sleep(1500);
            }

            System.out.println(ANSI_Colors.RED + "El comercio cierra sus puertas a los clientes y empleados." + ANSI_Colors.RESET);
            cierre.compareAndExchange(true, false);


        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    public void ingresarParque() {
        // EL ingreso al parque se controla con un semáforo (que serían molinetes)
        // La cantidad de molinetes varía
        // POdría hacer un lock? para mantenerlos ahí mientras el comercio está cerrado?

        try {

            if (estado.get()) { // A partir de las 6 no se puede entrar más

                cantidadMolinetes.acquire(1);

                System.out.println("El visitante  " + Thread.currentThread().getName() + " ingreso al parque.");

                cantidadMolinetes.release();

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);
        }

        // return estado.get() esto estará mal???. POdría hacer que vea el estado y
        // después ingresa.

    }
    // MONTAÑA RUSA. La atracción de montaña rusa tiene capacidad para 5 personas.
    // Debe estar llena para iniciar.
    // Existe un espacio de espera: Sí este se llena el visitante se va a otro lado.
    // La cantidad de el espacio de espera puede variar.

    private Semaphore esperaMontaña = new Semaphore(5);
    private Semaphore bajarMontaña = new Semaphore(0);
    private Semaphore sillasMontaña = new Semaphore(0);

    private Semaphore mutex = new Semaphore(5);
    private Semaphore ocupantes = new Semaphore(1);

    public void esperarMontañaRusa() {
        // Utilizo un semáforo para realizar el espacio de espera de la montaña rusa.

        try {
            if (estado.get() && esperaMontaña.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                // Lo realizo así el hilo no
                // queda atascado

                System.out.println("El visitante " + Thread.currentThread().getName()
                        + " ingresó a la sala de espera de la " +ANSI_Colors.CYAN +"  montaña rusa." + ANSI_Colors.RESET);

                subirMontañaRusa(); // Solo si ingresa puede subirse a la montaña rusa.

            } else {

                    System.out.println("El visitante " + Thread.currentThread().getName() + " se va de la atraccion" +  ANSI_Colors.CYAN + "  montaña rusa "+ ANSI_Colors.RESET);

                
            }

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " fue interrumpido.");
        }

    }

    public void subirMontañaRusa() {

        try {

            if(estado.get()){

            mutex.acquire(); // Una vez que sube
            esperaMontaña.release(); // Libera el lugar de la sala de espera.
            System.out.println("El visitante " + Thread.currentThread().getName() + " Se sentó en la " +ANSI_Colors.CYAN +"  montaña rusa." + ANSI_Colors.RESET);

            ocupantes.acquire();
            ocupantesMontaña++;
            System.out.println("Ocupantes: " + ocupantesMontaña + " /5");
            ocupantes.release();

            sillasMontaña.release(); // Le libero un permiso al empleado de la montaña rusa.

            bajarMontañaRusa(); // Preguntar si está bien poner estos metodos dentro de otros metodos.

            }else{

                System.out.println(Thread.currentThread().getName() + " se va de la "+ANSI_Colors.CYAN +"  montaña rusa " + ANSI_Colors.RESET + " porque el parque está cerrando..");
            }
         

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void bajarMontañaRusa() {

        try {

            bajarMontaña.acquire();
            System.out.println("El visitante " + Thread.currentThread().getName() + " se baja de la "+ANSI_Colors.CYAN +"  montaña rusa." + ANSI_Colors.RESET);


            if(estado.get()){ //SI todavía no son las 18 horas puede seguir entrando gente.

            mutex.release(); // Una vez que baja, puede subir otra persona de la sala de espera.


            }

        } catch (Exception e) {
        }

    }

    public void iniciarMontañaRusa() {

        try {

            if (estado.get()) {

                if (sillasMontaña.tryAcquire(5,5, TimeUnit.SECONDS)) {// Para que no quede colgada por si cierra el lugar.

                    System.out.println("La " +ANSI_Colors.CYAN +"  montaña rusa " + ANSI_Colors.RESET+ " está llena.. arranca");

                    Thread.sleep(500);

                    System.out.println("La "  +ANSI_Colors.CYAN +"  montaña rusa " + ANSI_Colors.RESET + " terminó su recorrido");

                    ocupantesMontaña = 0;
                    bajarMontaña.release(5);
                } else {
                    System.out.println("No llegaron pasajeros suficientes y la "  +ANSI_Colors.CYAN + "  montaña rusa " + ANSI_Colors.RESET + " no puede iniciar.");
                }

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }





    // Los autos chocadores son en total 10 autos, y cada uno requiere dos personas.
    // Comienza solo cuándo todos los autos están ocupados.

    private Semaphore subirAuto = new Semaphore(20), encenderAuto = new Semaphore(0);
    private Semaphore bajarAuto = new Semaphore(0);
    private Semaphore mut = new Semaphore(1);
    private int cantidadP = 0;

    public void subirAutoChocador() {

        try {

            mut.acquire();
            cantidadP++;
            mut.release();

            subirAuto.acquire();

            if (estado.get()) {

                System.out.println("El visitante " + Thread.currentThread().getName() + " Se subió a un " + ANSI_Colors.BLUE
                        + " auto chocador" + ANSI_Colors.RESET );

                encenderAuto.release(); // Deben liberar 20 permisos

                bajarAutoChocador(); // Lo pogno acá ya que solo se sube al auto si entra a este método

            } else {
                System.out.println(Thread.currentThread().getName() + " Se va de los + " + ANSI_Colors.BLUE
                        + " autos chocadores " + ANSI_Colors.RESET + " porque cerró el parque.");
            }

        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    public void bajarAutoChocador() {

        try {

            bajarAuto.tryAcquire(5, TimeUnit.SECONDS); // acquireINterrupted sirve?

            System.out.println("El visitante " + Thread.currentThread().getName()
                    + " se baja de la atracción" + ANSI_Colors.BLUE + " autos chocadores" + ANSI_Colors.RESET +":)");
            mut.acquire();
            cantidadP--;
            mut.release();
            subirAuto.release(); // Por cada uno que se baja, se puede subir otro

        } catch (InterruptedException e) {
            // TODO: handle exception
        }
    }

    public void encenderAutoC() {

        try {

            if (estado.get() && encenderAuto.tryAcquire(20, 6, TimeUnit.SECONDS)) {

                System.out.println("La atracción de "+ ANSI_Colors.BLUE + " auto chocadores" + ANSI_Colors.RESET +" inició..");

                detenerAutoC();

            } else {
                if (!estado.get()) { // Sí está cerrado..
                    if (cantidadP > 0) {

                        subirAuto.release(cantidadP); //Los liberamos para que se vayan.

                    }

                }
            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        }

    }

    public void detenerAutoC() {

        try {

            System.out.println("La atracción de " + ANSI_Colors.BLUE + " autos chocadores" + ANSI_Colors.RESET +" se detiene.");
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

    private AtomicBoolean subirBarco = new AtomicBoolean(true);
    private AtomicBoolean bajarBarco = new AtomicBoolean(false);

    public void subirBarcoPirata() { // Como no dice que ingresan por orden de llegada, utilizo un lock.

        barcoPirata.lock();
        try {
            while (subirBarco.get() == false) {
                visitantes.await();
            }

            if (estado.get()) { // Si está abierto el parque

                cantidadBarco++;
                System.out.println("El visitante " + Thread.currentThread().getName()
                        + " ingresó al barco pirata. Cantidad: " + cantidadBarco);

                if (cantidadBarco == 20) {

                    subirBarco.set(false);
                }

            } else {

                System.out.println(
                        "El visitante " + Thread.currentThread() + " debe irse del barco porque cerró el parque.");
            }

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void bajarBarcoPirata() { // Como no dice que ingresan por orden de llegada, utilizo un lock
        // Puedo hacer que los que no pueden ingresar simplemente pasen de largo?
        barcoPirata.lock();
        try {
            if (estado.get()) { // Si está abierto peude bajarse.

                while (!bajarBarco.get()) { // Espera a que el barco termine
                    barco.await();

                }

                cantidadBarco--;
                System.out.println("El visitante " + Thread.currentThread().getName()
                        + " bajó del barco pirata. Cantidad:" + cantidadBarco);

                if (cantidadBarco == 0) {

                    System.out.println("El visitante " + Thread.currentThread().getName() + " es el último en bajar");
                    bajarBarco.set(false); // No puede bajar más nadie.
                    visitantes.signalAll(); // Despertamos a todos los que están esperando los que están en el barco.

                }

            }

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void iniciarBarcoPirata() {

        barcoPirata.lock();
        try {

            barcoP.await(10, TimeUnit.SECONDS); // Espera 10 segundos
            subirBarco.set(false);

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

            bajarBarco.set(true);
            subirBarco.set(true);

            barco.signalAll();

        } finally {
            barcoPirata.unlock();

        }

    }

    private Exchanger<String> intercambioF = new Exchanger<>();
    private Exchanger<String> intercambioP = new Exchanger<>();

    // Debería haber dos encargados y dos exchanger

    /*
     * Área de Juegos de Premios (Obligatorio)
     * a. Los visitantes pueden participar en juegos de premios, pero deben
     * intercambiar
     * fichas con los encargados del área para poder jugar. Un visitante debe darle
     * una
     * ficha al encargado, y a cambio recibirá por haber jugado, un premio, este
     * dependerá de los puntos que obtenga en el juego. Cuantos más puntos, más
     * grande es el premio.
     */

    /*
     * Utilizo un semáforo para controlar que dos vistantes no se intercambien entre
     * sí. Entran de a uno.
     */

    private Semaphore visitFicha = new Semaphore(1);

    public void cambiarFichaV() {

        String pase;

        try {
            visitFicha.acquire();

            pase = intercambioF.exchange(Thread.currentThread().getName(), 5, TimeUnit.SECONDS); // Espera 5 segundos.

            System.out.println("El hilo " + Thread.currentThread().getName() + " compró una ficha. Obtuvo un " + pase);

            cambiarFichaJugar();

        } catch (InterruptedException a) {

        } catch (TimeoutException a) {

            System.out.println("El hilo " + Thread.currentThread().getName() + " se cansó de esperar y se va..");
            visitFicha.release();

        }

    }

    public void cambiarFichaJugar() {
        String premio;
        try {
            premio = intercambioP.exchange(Thread.currentThread().getName(), 5, TimeUnit.SECONDS); // Espera 5 segundos
                                                                                                   // para que no haya
                                                                                                   // deadlock

            System.out.println("El visitante " + Thread.currentThread().getName()
                    + " intercambió una ficha de juego para obtener un " + premio);
            visitFicha.release();

            // El jugar sería un sleep

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {
            System.out
                    .println("El hilo " + Thread.currentThread().getName() + " no pudo intercambiar su ficha y se va.");
            visitFicha.release();

        }

    }

    public void cambiarFichaE() { // COmo es un hilo solo que ejecuta, no necesito sincronizarlo.

        String ficha;

        try {

            if (estado.get()) {

                ficha = intercambioF.exchange("pase", 2, TimeUnit.SECONDS);

                System.out.println("El encargado vendió una ficha a " + ficha);

                cambiarPremioE();

            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            System.out.println("El encargado no intercambiar nada, porque no hay ningún visitante.");
        }

    }

    public void cambiarPremioE() {
        String f;

        try {
            f = intercambioP.exchange("premio " + generarPremio(), 2, TimeUnit.SECONDS);

            System.out.println("El encargado intercambió un premio con " + f);

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            System.out.println("El encargado no pudo intercambiar ningún premio.");
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

    /*
     * Comedor del Parque (Obligatorio)
     * a. En elcomedor, los visitantes se sientan en grupospara almorzar, el comedor
     * puede contener muchas mesas, pero tiene una peculiar restricción, cuando se
     * llena
     * una mesa de 4 personas, recién ahí todos comienzan a comer al mismo tiempo
     * (los
     * de la misma mesa). Utilicen un mecanismo de sincronización para asegurarse de
     * que el almuerzo no comienza hasta que la mesa esté completa. Recuerde además
     * que si el comedor está lleno. La gente puede esperar o se va.
     */

    private int cantidadPersonasComiendo = 0;

    private int cM = 4;

    private Semaphore cantidadMesas = new Semaphore(4 * cM); // Para tener el conteo de personas que están esperando.

    private CyclicBarrier mesa = new CyclicBarrier(4);

    public void ingresarComedor() {

        try {

            if (estado.get() && cantidadMesas.tryAcquire(1, 5, TimeUnit.SECONDS)) {// Trata de entrar al comedor 5
                                                                                   // segundos si esta abierto.

                System.out.println("El hilo " + Thread.currentThread().getName() + " ingresa al comedor");

                mesa.await(); // Acá esperan y pasan recién cuándo hay 4 hilos.

                System.out.println("La persona" + Thread.currentThread().getName() + " ya está comiendo :D");
                Thread.sleep(1000); // No afecta en nada creo´
                cantidadMesas.release();

            } else {
                System.out.println("La persona " + Thread.currentThread().getName()
                        + " se cansó de esperar en el comedor y se va..");
            }

        } catch (InterruptedException e) {
            // TODO: handle exception

            cantidadMesas.release();
        } catch (BrokenBarrierException e) { // Si no puede ingresar, solo deja libre el lugar del comedor.

            cantidadMesas.release();

        }

    }

    /*
     * Para recorrer el parque, los visitantes pueden abordar un tren turístico que
     * recorre
     * las instalaciones. Los visitantes se colocan en una cola para esperar que el
     * tren
     * llegue. El tren tiene capacidad para 10 personas y parte una vez que la fila
     * está
     * llena o después de 5 minutos de espera, lo que ocurra primero.
     */
    BlockingQueue<Thread> tren = new ArrayBlockingQueue<>(10);

    private Lock esperarTren = new ReentrantLock(); // Utilizo un monitor para crear la sala de espera.

    private Condition salaEspera = esperarTren.newCondition();

    private AtomicBoolean bandera = new AtomicBoolean(false);

    public void subirTren() {

        try {

            esperarTren.lock();

            while (!bandera.get()) { // Mientras no pueda ingresar.

                System.out.println(Thread.currentThread().getName() + " está esperando el tren.. ");
                salaEspera.await();

            }

            if (estado.get()) {

                tren.put(Thread.currentThread());

                System.out.println(Thread.currentThread().getName() + " se subió al tren. ");

                bajarTren();

            } else {// SI está cerrado..

                System.out.println("El parque ya cerró y " + Thread.currentThread().getName() + " se va del parque.");
            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        } finally {
            esperarTren.unlock();
        }

    }

    private Condition esperar = esperarTren.newCondition();
    private boolean bajar = false;

    public void bajarTren() {

        try {

            esperarTren.lock();

            esperar.await();

            System.out.println(Thread.currentThread().getName() + " se bajó del tren.");

        } catch (InterruptedException e) {
        } finally {
            esperarTren.unlock();

        }

    }

    private Condition arrancarTren = esperarTren.newCondition();

    public void encenderTren() {

        try {

            esperarTren.lock();

            if (estado.get()) { // Sí el parque está abierto...
                System.out.println("El tren arrancará en unos minutos...");
                bandera.compareAndExchange(false, true); // Permite la entrada a los visitantes.

                salaEspera.signalAll(); // Despierta a todos si había alguien esperando.

                long inicio = System.currentTimeMillis();
                long limite = inicio + 2000; // 2 segundos

                // Esperar hasta que se llenen los 10 lugares o pase el tiempo
                while (tren.size() < 10 && System.currentTimeMillis() < limite) { // SI cierra cuándo esta esperando,
                                                                                  // este sería el ultimo viaje.
                    arrancarTren.await(1, TimeUnit.SECONDS); // Duerme 1 segundo.

                }

                bandera.set(false); // Evitamos el paso de más gente.

                System.out.println("El tren arrancó.");

            } else {
                System.out.println("El parque ya cerró y las personas en la sala de espera deben irse.");
                bandera.compareAndExchange(false, true);
                salaEspera.signalAll();

            }

        } catch (InterruptedException e) {
        } finally {
            esperarTren.unlock();
        }

    }

    public void bajarPasajeros() {
        int aux = tren.size();

        try {
            esperarTren.lock();

            System.out.println("El tren terminó su viaje y se bajan los pasajeros...");

            esperar.signalAll();

            bandera.set(true);

            salaEspera.signalAll();

        } finally {
            esperarTren.unlock();
        }

    }

    public boolean getEstado() {

        return estado.get();
    }

    /*
     * Realidad Virtual
     * a.El parque ofrece una actividad de realidad virtual,donde los visitantes
     * necesitan
     * un equipo completo compuesto de un visor de realidad virtual (VR),dos
     * manoplas, y una base. El encargado de la atracción debe proporcionar estos
     * tres
     * elementos a cada visitante antes de que pueda participar. La cantidad de cada
     * elemento es limitada y puede variar, pero solo se puede permitir el ingreso
     * si el visitante tiene un equipo completo. Deben asegurarse de que los
     * visitantes
     * reciban los equipos completos antes de ingresar a la actividad. Si falta
     * algún componente, el visitante debe esperar hasta que esté disponible.
     */

    private int visores = 4, manoplas = 4, bases = 4;
    private boolean manoplaLista = false, baseLista = false, esperaLista = false;
    private int cantidadSala = 0, cantidadManopla = 0, cantidadBAse = 0;

    private Lock juegoVRLock = new ReentrantLock();

    private Condition salaEsperaVR = juegoVRLock.newCondition();
    private Condition manopla = juegoVRLock.newCondition();
    private Condition base = juegoVRLock.newCondition();
    private Condition jugarVR = juegoVRLock.newCondition();

    private boolean salir = false;

    public void ingresarVR() {

        juegoVRLock.lock();

        try {

            // Primero debe obtener el visor.

            System.out.println(Thread.currentThread().getName() + " está esperando en la sala de VR.");
            cantidadSala++;
            salaEsperaVR.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(Thread.currentThread().getName() + " pudo avanzar y obtiene un visor.");
                cantidadSala--;
                esperarManoplas();
            } else {
                System.out.println(Thread.currentThread().getName() + " se va porque el parque está cerrado.");
            }

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public void esperarManoplas() {

        juegoVRLock.lock();

        try {

            // Espera manoplas libres

            System.out.println(Thread.currentThread().getName() + " está esperando dos manoplas.");
            cantidadManopla++;
            manopla.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(Thread.currentThread().getName() + " pudo avanzar y tiene un visor y dos manoplas.");
                cantidadManopla--;
                esperarBase();

            } else {
                System.out.println(Thread.currentThread().getName() + " se va porque el parque está cerrado.");
            }

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public void esperarBase() {

        juegoVRLock.lock();

        try {

            // Espera base libre

            System.out.println(Thread.currentThread().getName() + " está esperando una base.");
            cantidadBAse++;
            base.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(
                        Thread.currentThread().getName() + " pudo avanzar y tiene un visor, dos manoplas y la base.");
                cantidadBAse--;
                jugarYDevolverEquipo();

            } else {
                System.out.println(Thread.currentThread().getName() + " se va porque el parque está cerrado.");
            }

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public void jugarYDevolverEquipo() {

        juegoVRLock.lock();

        try {

            System.out.println(Thread.currentThread().getName() + " juega en VR...");
            jugarVR.await(3, TimeUnit.SECONDS);

            System.out.println(Thread.currentThread().getName() + " devuelve el equipo.");
            manoplas = manoplas + 2;
            bases = bases + 1;
            visores = visores + 1;

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public synchronized void darEquipo() {

        juegoVRLock.lock();

        try {

            if (cantidadSala > 0) {
                if (visores > 0) {
                    visores--;

                    System.out.println("El empleado entrega un visor. Cantidad visores " + visores);
                    salaEsperaVR.signal();

                }
            }

            if (cantidadManopla > 0) {
                if (manoplas > 2) {
                    manoplas = manoplas - 2;
                    System.out.println("El empleado entrega dos manoplas. Cantidad manoplas: " + manoplas);

                    manopla.signal();
                }
            }

            if (cantidadBAse > 0) {

                if (bases > 1) {
                    bases--;
                    System.out.println("EL empleado entrega una base. Cantidad bases: " + base);

                    base.signal();

                }

            }

        } finally {
            juegoVRLock.unlock();
        }

    }

    public void cerrarLocalVR() {
        juegoVRLock.lock();

        try {
            System.out.println("El parque de diversiones ya cerró y el empleado cierra el local de VR.");

            salaEsperaVR.signalAll();
            manopla.signalAll();
            base.signalAll();

        } finally {

            juegoVRLock.unlock();
        }

    }

}
