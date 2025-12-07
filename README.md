# TP Final – Programación Concurrente
Simulación de un parque de diversiones utilizando hilos y distintas técnicas de sincronización en Java.

Este trabajo práctico consiste en modelar el funcionamiento de un parque de diversiones donde cada atracción, cada empleado y cada visitante funciona como un hilo independiente. 
La idea principal fue coordinar el acceso a los recursos compartidos (asientos, autos, equipos VR, etc.) y manejar correctamente los tiempos, 
la apertura/cierre del parque y la interacción entre visitantes y empleados.

La simulación incluye varias atracciones: Barco Pirata, Montaña Rusa, Juegos de Premio, Tren, Realidad Virtual y Autos Chocadores.
Cada una tiene su propio empleado (también un hilo) que maneja la lógica de la atracción: cuándo abre, cuántas personas pueden usarla, cuándo se enciende, cuánto dura, etc.

---

## Cómo funciona

En el `main` agregué un menú simple donde el usuario puede elegir qué actividad quiere probar. Si ingresa un número del 1 al 6, se ejecuta solo esa atracción 
junto con un grupo de visitantes que usan solamente esa actividad.  
Si elige la opción 7, se activan todas las atracciones a la vez y los visitantes van recorriéndolas de manera aleatoria.

También se agregó la opción **Q**, que sirve para cerrar el parque y terminar toda la ejecución, matando todos los hilos activos.

Los visitantes pueden decidir antes de entrar si van al parque o al shopping. Cuando el parque está cerrado esperan, y cuando abre ingresan 
y comienzan a usar las atracciones según la opción elegida en el menú.

---

## Qué utilicé

A lo largo del TP fui usando distintas herramientas de concurrencia:

- **Threads y Runnable** para modelar visitantes y empleados.  
- **Locks y Conditions** para coordinar momentos específicos (por ejemplo, esperar que el barco se llene o que un visitante tenga permiso para subir).  
- **Semáforos** en las atracciones que tienen recursos limitados (autos, asientos, equipos VR).  
- **AtomicBoolean** y variables atómicas para estados compartidos como la apertura del parque.  
- Algunos **métodos sincronizados** en situaciones donde la exclusión mutua era simple.

El objetivo principal fue asegurar que todos los hilos interactúen correctamente sin condiciones de carrera, sin deadlocks y respetando las reglas de cada atracción.

---

