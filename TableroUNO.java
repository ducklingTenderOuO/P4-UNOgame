/**
 * Write a description of class TableroUNO here.
 * Mantiene el flujo del juego
 * @author (AYJB)
 * @version (MAY 2025)
 */

import javax.swing.*;
import java.util.*;

public class TableroUNO {
    private final BarajaDeUNO baraja;
    private Jugador[] jugadores;
    private final ArrayList<CartaUNO> cartasJugadas = new ArrayList<>();
    private int turnoActual = 0;
    private int direccionJuego = 1; // 1 hor, - ant hor
    private final Scanner scanner = new Scanner(System.in);
    private final VentanaUNO ventana = new VentanaUNO();
    private boolean juegoTerminado = false;

    public TableroUNO() {
        baraja = new BarajaDeUNO();
        System.out.print("Número de jugadores (2-10): ");
        int numeroJugadores = scanner.nextInt();
        generarJugadores(numeroJugadores);
        mostrarManosIniciales();
        comenzarJuego();
    }

    private void mostrarManosIniciales() {
        System.out.println("\n=== MANOS INICIALES ===");
        for (Jugador jugador : jugadores) {
            System.out.println(jugador.getNombre() + ": " + jugador.getMano());
        }
    }

    private void generarJugadores(int numeroJugadores) {
        jugadores = new Jugador[numeroJugadores];
        for (int i = 0; i < numeroJugadores; i++) {
            System.out.print("Nombre jugador " + (i + 1) + ": ");
            String nombre = scanner.next();
            ArrayList<CartaUNO> mano = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                mano.add(baraja.getCartas().removeFirst());
            }
            jugadores[i] = new Jugador(nombre, mano);
        }
    }

    private void comenzarJuego() {
        CartaUNO primeraCarta;
        do {
            primeraCarta = baraja.getCartas().removeFirst();
        } while (primeraCarta.getColor().equals("negro"));

        cartasJugadas.add(primeraCarta);
        System.out.println("\n=== PRIMERA CARTA ===");
        System.out.println(primeraCarta);
        turno();
    }

    private void turno() {
        while (!juegoTerminado) {
            Jugador jugadorActual = jugadores[turnoActual];
            CartaUNO cartaMesa = cartasJugadas.getLast();

            System.out.println("\n=== TURNO DE " + jugadorActual.getNombre().toUpperCase() + " ===");
            System.out.println("Carta en mesa: " + cartaMesa);
            ventana.mostrarCartaActual(cartaMesa);

            // ¿Ganó?
            if (jugadorActual.getMano().isEmpty()) {
                mostrarFinDelJuego(jugadorActual);
                juegoTerminado = true;
                return;
            }

            CartaUNO ultimaRobada = jugadorActual.getUltimaRobada();
            if (ultimaRobada != null && ultimaRobada.esJugableSobre(cartaMesa)) {
                System.out.println("Jugando carta robada: " + ultimaRobada);
                jugarCarta(jugadorActual, ultimaRobada);
                continue;
            }

            ventana.actualizarTitulo(jugadorActual.getNombre());
            ventana.mostrarCartas(jugadorActual.getMano());

            System.out.println("Haz clic en una carta para jugarla, o robaaaa.");

            while (ventana.getCartaSeleccionada() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            CartaUNO seleccionada = ventana.getCartaSeleccionada();
            ventana.setCartaSeleccionada(null); // limpiar para el siguiente turno

            if (seleccionada != null && seleccionada.esJugableSobre(cartaMesa)) {
                jugarCarta(jugadorActual, seleccionada);
            } else {
                System.out.println("Carta inválida o no seleccionada. Robando una carta...");
                robarCarta(jugadorActual);
            }
        }
    }

    private void robarCarta(Jugador jugador) {
        // jugador roba hasta que pueda jugar la carta
        while (true) {
            robarCartas(jugador, 1); // Roba 1 carta
            System.out.println(jugador.getNombre() + " robó una carta.");

            // puede jugarla?
            CartaUNO cartaRobada = jugador.getMano().getLast();
            if (cartaRobada.esJugableSobre(cartasJugadas.getLast())) {
                System.out.println("¿Quieres jugar esta carta? (s/n)");
                if (scanner.next().equalsIgnoreCase("s")) {
                    jugarCarta(jugador, cartaRobada);
                } else {
                    siguienteTurno();  // Si no la juega, pasa el turno
                }
                break;
            }
        }
    }

    private void robarCartas(Jugador jugador, int cantidad) {
        for (int i = 0; i < cantidad && !baraja.getCartas().isEmpty(); i++) {
            CartaUNO robada = baraja.getCartas().removeFirst();
            jugador.getMano().add(robada);
            System.out.println("Robó: " + robada);
        }
    }

    /**
     * Añade la carta al montón, la remueve de la mano del jugador, y aplica sus efectos
     * También verifica si el jugador gano
     */

    private void jugarCarta(Jugador jugador, CartaUNO carta) {
        cartasJugadas.add(carta);
        jugador.getMano().remove(carta);
        jugador.setUltimaRobada(null);
        System.out.println(jugador.getNombre() + " jugó: " + carta);

        // comodines:D
        boolean saltoAdicional = false;
        switch (carta.getNumero()) {
            case 10 -> saltoAdicional = aplicarNegarTurno();
            case 11 -> aplicarCambioSentido();
            case 12 -> saltoAdicional = aplicarMas2();
            case 13, 14 -> saltoAdicional = aplicarComodin(carta);
        }

        if (jugador.getMano().isEmpty()) {
            mostrarFinDelJuego(jugador);
        } else if (!saltoAdicional) {
            siguienteTurno();
        }
    }

    /**
     * Aplica el efecto de una carta "Negar Turno", haciendo que el siguiente jugador pierda su turno.
     */
    private boolean aplicarNegarTurno() {
        System.out.println("¡El siguiente jugador pierde su turno!");
        siguienteTurno();
        return true;
    }

    /**
     * Cambia la dirección del juego de sentido horario a antihorario o viceversa
     */
    private void aplicarCambioSentido() {
        direccionJuego *= -1;  // Cambia la dirección del turno
        System.out.println("¡El sentido del juego ha cambiado!");
    }

    /**
     * El siguiente jugador robe dos cartas y pierda su turno.
     */
    private boolean aplicarMas2() {
        Jugador siguiente = obtenerSiguienteJugador();
        robarCartas(siguiente, 2);
        System.out.println("¡" + siguiente.getNombre() + " roba 2 cartas!");
        siguienteTurno();
        return true;
    }

    /**
     * Realiza el cambio de color y si la carta es "+4", el siguiente jugador robe cuatro cartas y
     * pierde su turno
     */
    private boolean aplicarComodin(CartaUNO comodin) {
        System.out.println("Elige un color (rojo/amarillo/verde/azul): ");
        String nuevoColor;
        do {
            nuevoColor = scanner.next().toLowerCase();
        } while (!Arrays.asList(CartaUNO.COLORES).contains(nuevoColor) || nuevoColor.equals("negro"));

        comodin.setColor(nuevoColor);
        System.out.println("Color cambiado a: " + nuevoColor);

        if (comodin.getNumero() == 14) {
            Jugador siguiente = obtenerSiguienteJugador();
            robarCartas(siguiente, 4);
            System.out.println("¡" + siguiente.getNombre() + " roba 4 cartas!");
            siguienteTurno();
            return true;
        }
        return false;
    }

    private Jugador obtenerSiguienteJugador() {
        return jugadores[(turnoActual + direccionJuego) % jugadores.length];
    }

    private void siguienteTurno() {
        turnoActual = (turnoActual + direccionJuego) % jugadores.length;
        if (turnoActual < 0) turnoActual = jugadores.length - 1;

        int numero = cartasJugadas.getLast().getNumero();
        if (numero == 10 || numero == 12 || numero == 14) {
            // Saltar otro turno
            turnoActual = (turnoActual + direccionJuego) % jugadores.length;
        }

        turno();
    }

    private void mostrarFinDelJuego(Jugador ganador) {
        System.out.println("\n¡" + ganador.getNombre() + " HA GANADO EL JUEGO!");
        System.out.println("\n=== CARTAS RESTANTES ===");
        for (Jugador jugador : jugadores) {
            if (!jugador.getMano().isEmpty()) {
                System.out.println(jugador.getNombre() + ": " + jugador.getMano());
            }
        }
        ventana.actualizarTitulo("Fin del juego");
        ventana.mostrarCartas(new ArrayList<>());
        ventana.mostrarCartaActual(null);

        JOptionPane.showMessageDialog(
                ventana,
                ganador.getNombre() + " ha ganado el juegoooo 🎉",
                "¡Juego terminadoooo!",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        new TableroUNO();
    }
}
