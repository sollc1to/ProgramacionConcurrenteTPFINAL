
public class Main {
    public static void main(String[] args) {

        ParqueDiversiones parque = new ParqueDiversiones();

        Thread barcoP = new Thread(new BarcoPirata(parque));

        barcoP.start();

        Thread montana = new Thread(new MontañaRusa(parque));

        montana.start();

        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();
        Thread reloj = new Thread(new Reloj(parque));
        reloj.start();

        Thread auto = new Thread(new AutoChocador(parque));
        auto.start();






        Thread empleadoF = new Thread(new EmpleadoFicha(parque));
        Thread empleadoP = new Thread(new EmpleadoPremio(parque));



        empleadoF.start();
        empleadoP.start();


        for (int i = 0; i < 20; i++) {

            try {
                (new Thread(new Visitante(parque), "" + i)).start();


            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }

}
