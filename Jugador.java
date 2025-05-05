import java.util.ArrayList;
import java.util.Scanner;

public class Jugador {
    private final String nombre;
    private final ArrayList<CartaUNO> mano;
    private CartaUNO ultimaRobada;

    public Jugador(String nombre, ArrayList<CartaUNO> mano) {
        this.nombre = nombre;
        this.mano = mano;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<CartaUNO> getMano() {
        return mano;
    }

    public CartaUNO seleccionarCarta(Scanner scanner, CartaUNO cartaActual) {
        System.out.println("Tus cartas:");
        //ventana.mostrarCartas(mano);
        for (int i = 0; i < mano.size(); i++) {
            System.out.println((i + 1) + ". " + mano.get(i));
        }

        while (true) {
            System.out.print("Elige una carta (1-" + mano.size() + ") o 0 para robar: ");
            if (scanner.hasNextInt()) {
                int opcion = scanner.nextInt();
                if (opcion == 0) {
                    return null;
                } else if (opcion >= 1 && opcion <= mano.size()) {
                    CartaUNO seleccionada = mano.get(opcion - 1);
                    if (seleccionada.esJugableSobre(cartaActual)) {
                        return seleccionada;
                    }
                    System.out.println("¡Carta no válida! Debe coincidir en color/número o ser comodín.");
                }
            } else {
                scanner.next();
            }
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }

    public CartaUNO getUltimaRobada() {
        return ultimaRobada;
    }

    public void setUltimaRobada(CartaUNO carta) {
        this.ultimaRobada = carta;
    }
}