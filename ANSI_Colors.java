


public class ANSI_Colors {
    // Colores de texto
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
// Colores de texto (extendidos/brillantes)
public static final String BRIGHT_BLACK = "\u001B[90m";
public static final String BRIGHT_RED = "\u001B[91m";
public static final String BRIGHT_GREEN = "\u001B[92m";
public static final String BRIGHT_YELLOW = "\u001B[93m";
public static final String BRIGHT_BLUE = "\u001B[94m";
public static final String BRIGHT_PURPLE = "\u001B[95m";
public static final String BRIGHT_CYAN = "\u001B[96m";
public static final String BRIGHT_WHITE = "\u001B[97m";
    // Colores de fondo
    public static final String BLACK_BG = "\u001B[40m";
    public static final String RED_BG = "\u001B[41m";
    public static final String GREEN_BG = "\u001B[42m";
    public static final String YELLOW_BG = "\u001B[43m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String PURPLE_BG = "\u001B[45m";
    public static final String CYAN_BG = "\u001B[46m";
    public static final String WHITE_BG = "\u001B[47m";

    // Estilos adicionales
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String ITALIC = "\u001B[3m"; // Cursiva

        // Función para crear efecto arcoíris
    public static String rainbow(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Colores del arcoíris (en orden: rojo, naranja, amarillo, verde, azul, índigo, violeta)
        String[] rainbowColors = { RED, YELLOW, GREEN, CYAN, BLUE, PURPLE, RED };
        StringBuilder result = new StringBuilder();

        // Aplica un color diferente a cada carácter
        for (int i = 0; i < text.length(); i++) {
            String color = rainbowColors[i % rainbowColors.length];
            result.append(color).append(text.charAt(i));
        }
        // Resetea el color al final
        result.append(RESET);
        return result.toString();
    }
}