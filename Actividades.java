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

public class Actividades { // EN las actividas incluyo: Juegos de VR, intercambio de fichas, y el tren.

    private Semaphore visitFicha = new Semaphore(1);
    private Exchanger<String> intercambioF = new Exchanger<>();
    private Exchanger<String> intercambioP = new Exchanger<>();
    private int cM;

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
    private ParqueDiversiones parque;

    public Actividades(ParqueDiversiones p, int cM) {
        parque = p;
        this.cM = cM;

        this.mesas = new CyclicBarrier[cM]; // mESAS TOTALES

        this.ocupacionMesa = new int[cM];

        for (int i = 0; i < cM; i++) {
            mesas[i] = new CyclicBarrier(4); // cada mesa espera 4 personas
            ocupacionMesa[i] = 0; // al inicio, todas vacías
        }

    }

    public void cambiarFichaV() {

        String pase;

        try {
            visitFicha.acquire(); // Lo uso para que se intercambien de A UNO

            pase = intercambioF.exchange(Thread.currentThread().getName(), 5, TimeUnit.SECONDS); // Espera 5 segundos.

            System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                    + Thread.currentThread().getName() + " compró una ficha. Obtuvo un " + pase);

            cambiarFichaJugar();

        } catch (InterruptedException a) {

        } catch (TimeoutException a) {

            if (parque.getEstado()) {

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

            if (parque.getEstado()) {

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

            if (parque.getEstado()) {

                ficha = intercambioF.exchange("pase", 2, TimeUnit.SECONDS);

                System.out.println(ANSI_Colors.YELLOW + " [Exchanger] " + ANSI_Colors.RESET
                        + "El encargado vendió una ficha a " + ficha);

                cambiarPremioE();

            }

        } catch (InterruptedException e) {
            // TODO: handle exception
        } catch (TimeoutException e) {

            if (parque.getEstado()) {

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

            if (parque.getEstado()) {

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



    private CyclicBarrier[] mesas; // una barrera por mesa (4 personas)
    private int[] ocupacionMesa; // cuántas personas hay sentadas en cada mesa

    private Semaphore mu = new Semaphore(1); // mutex para elegir mesa de forma segura


    private CyclicBarrier mesa = new CyclicBarrier(4);


    public void ingresarComedor() {

        String nombre = Thread.currentThread().getName();
        int mesaElegida = -1;

        try {
            // Si el parque está cerrado, ni entra
            if (!parque.getEstado()) {
                System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                        + nombre + " no puede entrar al comedor porque el parque está cerrado.");

            }
            {

                // Elegir una mesa con lugar (ocupación < 4)
                mu.acquire(); // sección crítica para elegir mesa
                int i = 0;
                boolean ocupa = false;
                while (i < cM && !ocupa) {
                    if (ocupacionMesa[i] < 4) { // hay lugar en esta mesa
                        ocupacionMesa[i]++; // me siento
                        mesaElegida = i;
                        ocupa = true;
                    }
                    i++;
                }

                if (!ocupa) {
                    // No hay ninguna mesa con lugar
                    System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                            + nombre + " no encontró mesa libre y se va.");
                    mu.release();
                } else {

                    mu.release();

                    System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                            + nombre + " se sienta en la mesa " + mesaElegida + " y espera a los demás.");

                    // Espera a que se complete la mesa de 4
                    mesas[mesaElegida].await(); // cuando hay 4, todos avanzan
                    // Mesa elegida es una variable loca.

                    // Si llegó acá, la barrera se completó bien
                    System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET + nombre
                            + " está comiendo en la mesa " + mesaElegida + ".");
                    Thread.sleep(1000); // simula comer

                }

            }

        } catch (InterruptedException | BrokenBarrierException e) {
            // Cualquier problema (interrupción o barrera rota)
            System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                    + nombre + " no pudo comer por un problema en el comedor.");
            Thread.currentThread().interrupt();

        } finally {
            // Si llegó a sentarse en alguna mesa, libera su lugar
            if (mesaElegida != -1) {
                try {
                    mu.acquire();
                    ocupacionMesa[mesaElegida]--;
                    mu.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println(ANSI_Colors.RED + " [Comedor] " + ANSI_Colors.RESET
                        + nombre + " deja la mesa " + mesaElegida + ".");
            }
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
    private BlockingQueue<Thread> tren = new ArrayBlockingQueue<>(10);

    private Lock trenL = new ReentrantLock();

    private Condition esperarT = trenL.newCondition();
    private Condition bajarT = trenL.newCondition();
    private boolean subir = true;

    public void subirTren() {

        trenL.lock();

        try {

            while (tren.size() == 10 || !subir) {
                esperarT.await();
            }

            if (parque.getEstado()) { // Sí esta abierto

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " se subió al tren");

                tren.add(Thread.currentThread()); // SI no, ingresa al tren.

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " Cantidad gente en tren: " + tren.size());

                if (tren.size() == 10) {

                    empleadoTren.signal();
                } else {
                    esperarT.signal();

                }

                bajarTren();

            } else {
                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " no sube al tren porque cerró el parque.");
                esperarT.signal();

            }

        } catch (Exception e) {
        } finally {
            trenL.unlock();
        }

    }

    public void bajarTren() { // NO tomo el lock de nuevo pq el hilo ya lo tiene.

        try {
            bajarT.await();

            System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " se baja del tren.");

            tren.poll();

            if (tren.isEmpty()) {
                esperarT.signalAll();
            }

        } catch (Exception e) {
        }

    }

    // Controlar bien los hilos

    private Condition empleadoTren = trenL.newCondition();

    public void encenderTren() {

        trenL.lock();

        try {

            if (parque.getCierre()) { // Sí el parque está abierto...
                System.out.println(
                        ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El tren arrancará en unos minutos...");

                esperarT.signalAll(); // Despierta a todos si había alguien esperando.

                empleadoTren.await(6, TimeUnit.SECONDS); // Duerme 6 segundos.

                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET + "El tren arrancó.");

                subir = false;
            } else {
                System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET
                        + "El parque ya cerró y las personas en la sala de espera deben irse.");

                esperarT.signalAll();

            }

        } catch (InterruptedException e) {
        } finally {
            trenL.unlock();
        }

    }

    public void bajarPasajeros() {

        trenL.lock();

        int aux = tren.size();

        try {

            System.out.println(ANSI_Colors.GREEN + " [Tren] " + ANSI_Colors.RESET
                    + "El tren terminó su viaje y se bajan los pasajeros...");
            subir = true;

            bajarT.signalAll();

        } finally {
            trenL.unlock();
        }

    }

    public void bajarPasajerosT() { // POner comentario de q hilo lo ejecuta

        trenL.lock();

        try {
            subir = true;
            System.out.println("Como el parque cerró, el empleado baja a todos los pasajeros.");
            esperarT.signalAll();

        } finally {

            trenL.unlock();
        }

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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " está esperando en la sala de VR.");
            cantidadSala++;
            salaEsperaVR.await();

            if (parque.getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " pudo avanzar y obtiene un visor.");
                cantidadSala--;
                esperarManoplas();
            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " se va porque el parque está cerrado.");
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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " está esperando dos manoplas.");
            cantidadManopla++;
            manopla.await();

            if (parque.getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " pudo avanzar y tiene un visor y dos manoplas.");
                cantidadManopla--;
                esperarBase();

            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " se va porque el parque está cerrado.");
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

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " está esperando una base.");
            cantidadBAse++;
            base.await();

            if (parque.getEstado()) { // SI el negocio está abierto
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET +
                        Thread.currentThread().getName() + " pudo avanzar y tiene un visor, dos manoplas y la base.");
                cantidadBAse--;
                jugarYDevolverEquipo();

            } else {
                System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                        + " se va porque el parque está cerrado.");
            }

        } catch (InterruptedException e) {
        } finally {
            juegoVRLock.unlock();
        }

    }

    public void jugarYDevolverEquipo() {

        juegoVRLock.lock();

        try {

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " juega en VR...");
            jugarVR.await(3, TimeUnit.SECONDS);

            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET + Thread.currentThread().getName()
                    + " devuelve el equipo.");
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

                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET
                            + "El empleado entrega un visor. Cantidad visores " + visores);
                    salaEsperaVR.signal();

                }
            }

            if (cantidadManopla > 0) {
                if (manoplas > 2) {
                    manoplas = manoplas - 2;
                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET
                            + "El empleado entrega dos manoplas. Cantidad manoplas: " + manoplas);

                    manopla.signal();
                }
            }

            if (cantidadBAse > 0) {

                if (bases > 1) {
                    bases--;
                    System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET
                            + "EL empleado entrega una base. Cantidad bases: " + bases);

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
            System.out.println(ANSI_Colors.BOLD + " [VR] " + ANSI_Colors.RESET
                    + "El parque de diversiones ya cerró y el empleado cierra el local de VR.");

            salaEsperaVR.signalAll();
            manopla.signalAll();
            base.signalAll();

        } finally {

            juegoVRLock.unlock();
        }

    }

}
