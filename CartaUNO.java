public class CartaUNO {
    private String color;
    private final int numero;
    public static final String[] COLORES = {"rojo", "amarillo", "verde", "azul", "negro"};

    public CartaUNO(String color, int numero) {
        this.color = color;
        this.numero = numero;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        if (color.equals("negro")) {
            return (numero == 13) ? "[Comodín]" : "[Comodín +4]";
        }
        return "[" + color + " " + numero + "]";
    }

    public boolean esJugableSobre(CartaUNO otra) {
        return this.color.equals(otra.color) ||
                this.numero == otra.numero ||
                this.color.equals("negro");
    }

    public String getRutaImagen() {
        String nombre;
        switch (numero) {
            case 10 -> nombre = "_negarTurno";
            case 11 -> nombre = "_reverse";
            case 12 -> nombre = "_masDos";
            case 13 -> nombre = "_cambiarColor";
            case 14 -> nombre = "_masCuatro";
            default -> nombre = String.valueOf(numero);
        }
        return "imagenes/" + color + nombre + ".png";
    }
}
