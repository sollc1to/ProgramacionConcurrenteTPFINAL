import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AtraccionesMecanicas {
    //MUevo a este OBj compartido la montaña rusa, autos, y el barco pirata.


    private Semaphore esperaMontaña = new Semaphore(5);
    private Semaphore bajarMontaña = new Semaphore(0);
    private Semaphore sillasMontaña = new Semaphore(0);

    private Semaphore mutex = new Semaphore(5);
    private Semaphore ocupantes = new Semaphore(1);

    private int ocupantesMontaña = 0;

    private ParqueDiversiones parque;

    public AtraccionesMecanicas(ParqueDiversiones p){
        parque = p;

    }

    public void esperarMontañaRusa() {
        // Utilizo un semáforo para realizar el espacio de espera de la montaña rusa.

        try {
            if (parque.getEstado() && esperaMontaña.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
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

            if (parque.getEstado()) {

                mutex.acquire(); // Una vez que sube
                esperaMontaña.release(); // Libera el lugar de la sala de espera.
                System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + "El visitante "  + Thread.currentThread().getName() + " Se sentó en la  montaña rusa.");

                ocupantes.acquire();

                ocupantesMontaña++;
                System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET + "Ocupantes: "  + ocupantesMontaña + " /5");
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

            if (parque.getEstado()) { // SI todavía no son las 18 horas puede seguir entrando gente.

                mutex.release(); // Una vez que baja, puede subir otra persona de la sala de espera.

            }

        } catch (Exception e) {
        }

    }

    public void iniciarMontañaRusa() {

        try {

            if (parque.getEstado()) {

                if (sillasMontaña.tryAcquire(5, 5, TimeUnit.SECONDS)) {// Para que no quede colgada por si cierra el
                                                                       // lugar.

                    System.out.println(ANSI_Colors.CYAN + " [Montaña] " + ANSI_Colors.RESET     + "La  montaña rusa. está llena.. arranca");

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
    //COntrolar que bajan todos y recien ahi suben

    public void subirAutoChocador() {

        try {

            subirAuto.acquire();

            if (parque.getEstado()) { // Sí todavía sigue abierto el parque..

                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET + "El visitante " + Thread.currentThread().getName() + " se subió a un auto chocador.");

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



            if(cantidadP == 0){
                subirAuto.release(20); //EL último que se baja libera 10 permisos.
            }


            


        } catch (InterruptedException e) {
            // TODO: handle exception
        }
    }

   public void encenderAutoC() {

    try {
        while (parque.getEstado()) {  // mientras la atracción siga habilitada

            // Esperar a que llegue al menos una persona al auto
            encenderAuto.acquire();   // se desbloquea cuando al menos uno se subió

            // 2) Dar tiempo para que se sigan subiendo otros (máx 6 seg)
            Thread.sleep(6000);

            // 3) Leer cuántas personas hay actualmente en la atracción
            mut.acquire();
            int pasajeros = cantidadP;   // cantidad actual de personas arriba
            mut.release();

            if (pasajeros > 0) {
                System.out.println(ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET
                        + "La atracción de autos chocadores inició con " + pasajeros + " personas.");
                detenerAutoC(pasajeros);   // libera a esos pasajeros
            }
        }

        // Si el parque cerró, y aún hay gente arriba, los liberamos para que se bajen/retiren
        if (!parque.getEstado()) {
            mut.acquire();
            int restantes = cantidadP;
            mut.release();

            if (restantes > 0) {
                subirAuto.release(restantes);  // los que estaban esperando subir
                bajarAuto.release(restantes);  // los que estaban arriba esperando bajar
            }
        }

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}


    public void detenerAutoC(int p) {

        try {

            System.out.println(
                    ANSI_Colors.BLUE + " [Autos] " + ANSI_Colors.RESET
                            + " La atracción de  autos chocadores se detiene.");
            bajarAuto.release(p);

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

            if (parque.getEstado()) { // Si está abierto el parque

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
            if (parque.getEstado()) { // Si está abierto peude bajarse.

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

            barcoP.await(10, TimeUnit.SECONDS); // Espera 10 segundos. Podria haber un sleep.

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

  


    
}
