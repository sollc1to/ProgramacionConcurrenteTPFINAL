
public class Main {
    public static void main(String[] args) {

        ParqueDiversiones parque = new ParqueDiversiones(1,2);

    
     /*     Thread barcoP = new Thread(new BarcoPirata(parque));
        
         barcoP.start();

         
*/
                  
         Thread reloj = new Thread(new Reloj(parque));
         reloj.start();

        Thread montana = new Thread(new EmpleadoMontañaR(parque));

        montana.start();

        Thread empleado = new Thread(new Empleado(parque));
        empleado.start();
    /* 
        Thread empleadoPremio = new Thread(new EmpleadoPremio(parque));
        empleadoPremio.start();


        */

        Thread empleadoTren = new Thread(new Empleadotren(parque));
        empleadoTren.start();


        Thread empleadoVR = new Thread(new EmpleadoVR(parque));
        empleadoVR.start();



String[] nombres = {
    "Lucas", "María", "Sofía", "Juan", "Lautaro",
    "Camila"  , "Mateo", "Julieta", "Thiago", "Valentina",
    "Bruno", "Martina", "Benjamín", "Luciana", "Agustín",
    "Emma", "Santiago", "Isabella", "Franco", "Renata",
    "Tomás", "Mía", "Bautista", "Morena", "Felipe",
    "Zoe", "Joaquín", "Catalina", "Simón", "Abril",
    "Ramiro", "Elena", "Nicolás", "Guadalupe", "Gael",
    "Luna", "Axel", "Josefina", "Dylan", "Rocío",
    "Iker", "Paula", "Maximiliano", "Carolina", "Cristóbal",
    "Florencia", "Alex", "Ámbar", "Federico", "Alma",
    "Ezequiel", "Pilar", "Ian", "Daniela", "Kevin",
    "Malena", "Valentino", "Bianca", "Tiziano", "Sol",
    "Ignacio", "Candela", "León", "Milagros", "Darío",
    "Ariana", "Emilio", "Selena", "Hernán", "Noelia",
    "Pablo", "Agostina", "Gabriel", "Carla", "Rodrigo",
    "Victoria", "Elías", "Antonia", "Gonzalo", "Josefina",
    "Rafael", "Mora", "Alan", "Nadia", "Diego",
    "Celeste", "Fernando", "Amparo", "Hugo", "Camila",
    "Mauricio", "Regina", "Ulises", "Tamara", "Félix",
    "Aylén", "Oscar", "Jazmín", "Guido", "Nahir"
};


        for (int i = 0; i < nombres.length; i++) {
            try {
                Thread visitante = new Thread(new Visitante(parque), nombres[i]);
                visitante.start();

                visitante.sleep(1400);


            
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }

    }

}
