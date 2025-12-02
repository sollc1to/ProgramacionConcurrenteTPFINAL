
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

    public boolean getCierre() {
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

            System.out.println(
                    ANSI_Colors.RED + "El comercio cierra sus puertas a los clientes y empleados." + ANSI_Colors.RESET);
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

                System.out.println(ANSI_Colors.CYAN + " [Montaña]" + ANSI_Colors.RESET + " El visitante "
                        + Thread.currentThread().getName()
                        + " ingresó a la sala de espera de la  montaña rusa.");

                subirMontañaRusa(); // Solo si ingresa puede subirse a la montaña rusa.

            } else {

                System.out.println(ANSI_Colors.CYAN + " [Montaña]" + ANSI_Colors.RESET + "El visitante "
                        + Thread.currentThread().getName() + " se va de la atraccion  montaña rusa.");

            }

        } catch (InterruptedException e) {
            System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " fue interrumpido.");
        }

    }

    public void subirMontañaRusa() {

        try {

            if (estado.get()) {

                mutex.acquire(); // Una vez que sube
                esperaMontaña.release(); // Libera el lugar de la sala de espera.
                System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + "El visitante "
                        + Thread.currentThread().getName() + " Se sentó en la  montaña rusa.");

                ocupantes.acquire();
                ocupantesMontaña++;
                System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + "Ocupantes: "
                        + ocupantesMontaña + " /5");
                ocupantes.release();

                sillasMontaña.release(); // Le libero un permiso al empleado de la montaña rusa.

                bajarMontañaRusa(); // Preguntar si está bien poner estos metodos dentro de otros metodos.

            } else {

                System.out
                        .println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                                + " se va de la  montaña rusa porque el parque está cerrando..");
                esperaMontaña.release();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void bajarMontañaRusa() {

        try {

            bajarMontaña.acquire();
            System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + "El visitante "
                    + Thread.currentThread().getName() + " se baja de la  montaña rusa.");

            if (estado.get()) { // SI todavía no son las 18 horas puede seguir entrando gente.

                mutex.release(); // Una vez que baja, puede subir otra persona de la sala de espera.

            }

        } catch (Exception e) {
        }

    }

    public void iniciarMontañaRusa() {

        try {

            if (estado.get()) {

                if (sillasMontaña.tryAcquire(5, 5, TimeUnit.SECONDS)) {// Para que no quede colgada por si cierra el
                                                                       // lugar.

                    System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET
                            + "La  montaña rusa. está llena.. arranca");

                    Thread.sleep(500);

                    System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET
                            + "La  montaña rusa. terminó su recorrido");

                    ocupantesMontaña = 0;
                    bajarMontaña.release(5);
                } else {
                    System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET
                            + "No llegaron pasajeros suficientes y la montaña rusa  no puede iniciar.");
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

            subirAuto.acquire();

            if (estado.get()) { // Sí todavía sigue abierto el parque..

                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET + "El visitante "
                        + Thread.currentThread().getName() + " se subió a un auto chocador.");

                mut.acquire();
                cantidadP++;
                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET
                        + "Cantidad personas en autos chocadores: " + cantidadP + " /20");
                mut.release();

                encenderAuto.release(); // Deben liberar 20 permisos

                bajarAutoChocador(); // Lo pogno acá ya que solo se sube al auto si entra a este método

            } else {
                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " Se va de los  autos chocadores. porque cerró el parque.");
            }

        } catch (InterruptedException e) {
            Logger.getLogger(ParqueDiversiones.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    public void bajarAutoChocador() {

        try {

            bajarAuto.acquire();

            System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET + "El visitante "
                    + Thread.currentThread().getName() + " se baja de la atracción autos chocadores");

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

            if (estado.get() && encenderAuto.tryAcquire(20, 6, TimeUnit.MINUTES)) { // ESPERA UN MINUTO

                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET
                        + "La atracción de  autos chocadores inició..");

                detenerAutoC();

            } else {
                if (!estado.get()) { // Sí está cerrado..
                    if (cantidadP > 0) {

                        subirAuto.release(cantidadP); // Los liberamos para que se vayan.

                    }

                }
            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        }

    }

    public void detenerAutoC() {

        try {

            System.out.println(
                    ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET
                            + " La atracción de  autos chocadores se detiene.");

            bajarAuto.release(cantidadP);

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
                System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " ingresó al barco pirata. Cantidad: " + cantidadBarco);

                if (cantidadBarco == 20) {

                    subirBarco.set(false); // Sí ya subieron 20, ponemos la variable en false para que no entre más
                                           // gente.
                }

                bajarBarcoPirata();

            } else {

                System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " debe irse del  barco pirata porque cerró el parque.");
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
                System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " bajó del barco pirata Cantidad:" + cantidadBarco);

                if (cantidadBarco == 0) {

                    System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET + "El visitante "
                            + Thread.currentThread().getName() + " es el último en bajar");
                    bajarBarco.set(false); // No puede bajar más nadie.
                    visitantes.signalAll(); // Despertamos a todos los que están esperando los que están en el barco.

                }

            } else {

                System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " no pudo subir al barco pirata porque cerró el parque.");
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

            System.out
                    .println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET
                            + "El  barco pirata  inició su vuelta.");

        } catch (InterruptedException e) {

        } finally {
            barcoPirata.unlock();

        }

    }

    public void terminarBarcoPirata() {
        barcoPirata.lock();
        try {

            System.out.println(ANSI_Colors.PURPLE + " [Barco] " + ANSI_Colors.RESET + "El barco pirata terminó");

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
            visitFicha.acquire(); // Lo uso para que se intercambien de A UNO

            pase = intercambioF.exchange(Thread.currentThread().getName(), 5, TimeUnit.SECONDS); // Espera 5 segundos.

            System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET + "El hilo "
                    + Thread.currentThread().getName() + " compró una ficha. Obtuvo un " + pase);

            cambiarFichaJugar();

        } catch (InterruptedException a) {

        } catch (TimeoutException a) {

            if (estado.get()) {

                System.out
                        .println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                                + Thread.currentThread().getName() + " no pudo intercambiar su ficha y se va.");

            } else {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " no pudo intercambiar porque cerró el parque y se va.");
            }

            visitFicha.release();

        }

    }

    public void cambiarFichaJugar() {
        String premio;
        try {
            premio = intercambioP.exchange(Thread.currentThread().getName(), 5, TimeUnit.SECONDS); // Espera 5 segundos
                                                                                                   // para que no haya
                                                                                                   // // deadlock

            System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET + "El visitante "
                    + Thread.currentThread().getName()
                    + " intercambió una ficha de juego para obtener un " + premio);
            visitFicha.release();

            // El jugar sería un sleep

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            if (estado.get()) {

                System.out
                        .println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                                + Thread.currentThread().getName() + " no pudo intercambiar su ficha y se va.");

            } else {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " no pudo intercambiar porque cerró el parque y se va.");
            }

            visitFicha.release();

        }

    }

    public void cambiarFichaE() { // COmo es un hilo solo que ejecuta, no necesito sincronizarlo.

        String ficha;

        try {

            if (estado.get()) {

                ficha = intercambioF.exchange("pase", 2, TimeUnit.SECONDS);

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "El encargado vendió una ficha a " + ficha);

                cambiarPremioE();

            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            if (estado.get()) {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "El encargado de intercambios no vendió ninguna ficha.");

            } else {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "el encargado de intercambio se va porque cerró el parque.");
            }

        }

    }

    public void cambiarPremioE() {
        String f;

        try {
            f = intercambioP.exchange("premio " + generarPremio(), 2, TimeUnit.SECONDS);

            System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                    + "El encargado intercambió un premio con " + f);

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            if (estado.get()) {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "El encargado no pudo intercambiar ningún premio.");

            } else {

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "El no pudo intercambiar ninguna ficha porque cerró el parque.");
            }

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

    private int cM;

    private Semaphore cantidadSillas = new Semaphore(4 * cM); // Para tener el conteo de personas que están esperando.
    private Semaphore mu = new Semaphore(1);

    private CyclicBarrier mesa = new CyclicBarrier(4);

    private int cantidadM = 0;

    public void ingresarComedor() {

        try {
            // Verificar estado y tratar de adquirir la silla
            if (estado.get() && cantidadSillas.tryAcquire(1, 8, TimeUnit.SECONDS)) {

                System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                        + Thread.currentThread().getName() + " ingresa al comedor y espera a los demás.");
      
                // Esperar en la barrera para que lleguen los 4 hilos
                mesa.await(8, TimeUnit.SECONDS); 

                // Si la barrera se completa con éxito, los hilos avanzan a comer
                System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + "La persona "
                        + Thread.currentThread().getName() + " ya está comiendo.");
                Thread.sleep(1000);


            } else {
                // Manejo de fallos en la adquisición o si el parque cerró
                if (!estado.get()) {
                    System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                            + Thread.currentThread().getName() + " se va del comedor porque cerró el parque.");
                } else {
                    System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + "La persona "
                            + Thread.currentThread().getName() + " se cansó de esperar en el comedor y se va..");
                }
            }

        } catch (InterruptedException e) {
            // Re-interrumpir el hilo es una buena práctica
            Thread.currentThread().interrupt();
            System.out
                    .println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                            + " fue interrumpido y se retira.");

        }catch (TimeoutException e){

            System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + Thread.currentThread().getName() + " No pudo comer porque no llegó nadie.");
            cantidadSillas.release();

        } catch (BrokenBarrierException e) {
            // Ocurre si otro hilo falló o fue interrumpido.
            System.out
                    .println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                            + " no pudo comer porque la barrera se rompió.");

        } finally {
            // Liberar la silla si se adquirió, independientemente de si comió o no.

            cantidadSillas.release();
            System.out.println(
                    ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                            + " ha liberado la silla.");

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

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName() + " está esperando el tren.. ");
                salaEspera.await();

            }

            if (estado.get()) { //Sí esta abierto se sube al tren.

                tren.put(Thread.currentThread());

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName() + " se subió al tren. ");

                bajarTren();

            } else {// SI está cerrado..

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El parque ya cerró y " + Thread.currentThread().getName() + " se va del parque.");
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

            System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName() + " se bajó del tren.");

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
                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El tren arrancará en unos minutos...");
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

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El tren arrancó.");

            } else {
                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET +"El parque ya cerró y las personas en la sala de espera deben irse.");
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

            System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El tren terminó su viaje y se bajan los pasajeros...");

            esperar.signalAll();

            bandera.set(true); //Puede subir más gente.

            salaEspera.signalAll();

        } finally {
            esperarTren.unlock();
        }

    }


    public void cerrarTren(){

        System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET +"El empleado cierra la atracción de tren y se va a su casa.");

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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName() + " está esperando en la sala de VR.");
            cantidadSala++;
            salaEsperaVR.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " pudo avanzar y obtiene un visor.");
                cantidadSala--;
                esperarManoplas();
            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " se va porque el parque está cerrado.");
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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " está esperando dos manoplas.");
            cantidadManopla++;
            manopla.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " pudo avanzar y tiene un visor y dos manoplas.");
                cantidadManopla--;
                esperarBase();

            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " se va porque el parque está cerrado.");
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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " está esperando una base.");
            cantidadBAse++;
            base.await();

            if (getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +
                        Thread.currentThread().getName() + " pudo avanzar y tiene un visor, dos manoplas y la base.");
                cantidadBAse--;
                jugarYDevolverEquipo();

            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " se va porque el parque está cerrado.");
            }

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public void jugarYDevolverEquipo() {

        juegoVRLock.lock();

        try {

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " juega en VR...");
            jugarVR.await(3, TimeUnit.SECONDS);

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +Thread.currentThread().getName() + " devuelve el equipo.");
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

                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +"El empleado entrega un visor. Cantidad visores " + visores);
                    salaEsperaVR.signal();

                }
            }

            if (cantidadManopla > 0) {
                if (manoplas > 2) {
                    manoplas = manoplas - 2;
                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +"El empleado entrega dos manoplas. Cantidad manoplas: " + manoplas);

                    manopla.signal();
                }
            }

            if (cantidadBAse > 0) {

                if (bases > 1) {
                    bases--;
                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +"EL empleado entrega una base. Cantidad bases: " + base);

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
            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +"El parque de diversiones ya cerró y el empleado cierra el local de VR.");

            salaEsperaVR.signalAll();
            manopla.signalAll();
            base.signalAll();

        } finally {

            juegoVRLock.unlock();
        }

    }

}
