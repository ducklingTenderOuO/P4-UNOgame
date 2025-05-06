import java.util.*;

public class TableroUNO {
    private final BarajaDeUNO baraja;
    private Jugador[] jugadores;
    private final ArrayList<CartaUNO> cartasJugadas = new ArrayList<>();
    private int turnoActual = 0;
    private int direccionJuego = 1; // 1 hor, - ant hor
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private final VentanaUNO ventana = new VentanaUNO();

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
                mano.add(baraja.getCartas().remove(0));
            }
            jugadores[i] = new Jugador(nombre, mano);
        }
    }

    private void comenzarJuego() {
        CartaUNO primeraCarta;
        do {
            primeraCarta = baraja.getCartas().remove(0);
        } while (primeraCarta.getColor().equals("negro"));

        cartasJugadas.add(primeraCarta);
        System.out.println("\n=== PRIMERA CARTA ===");
        System.out.println(primeraCarta);
        turno();
    }

    private void turno() {
        while (true) {
            Jugador jugadorActual = jugadores[turnoActual];
            CartaUNO cartaMesa = cartasJugadas.get(cartasJugadas.size() - 1);

            System.out.println("\n=== TURNO DE " + jugadorActual.getNombre().toUpperCase() + " ===");
            System.out.println("Carta en mesa: " + cartaMesa);
            ventana.mostrarCartaActual(cartaMesa);

            // Ganeeee?
            if (jugadorActual.getMano().isEmpty()) {
                mostrarFinDelJuego(jugadorActual);
                break;
            }

            // card rob
            CartaUNO ultimaRobada = jugadorActual.getUltimaRobada();
            if (ultimaRobada != null && ultimaRobada.esJugableSobre(cartaMesa)) {
                System.out.println("Jugando carta robada: " + ultimaRobada);
                jugarCarta(jugadorActual, ultimaRobada);
                continue; // Pasar al siguiente turno
            }

            // card norm
            ventana.actualizarTitulo(jugadorActual.getNombre());
            ventana.mostrarCartas(jugadorActual.getMano());
            CartaUNO cartaJugada = jugadorActual.seleccionarCarta(scanner, cartaMesa);

            if (cartaJugada == null) {
                robarCarta(jugadorActual);
            } else {
                jugarCarta(jugadorActual, cartaJugada);
            }
        }
    }

    private void robarCarta(Jugador jugador) {
        // ju rob hasta que puede ju
        while (true) {
            robarCartas(jugador, 1); // Roba 1 carta
            System.out.println(jugador.getNombre() + " robó una carta.");

            // puede jugarla?
            CartaUNO cartaRobada = jugador.getMano().get(jugador.getMano().size() - 1);
            if (cartaRobada.esJugableSobre(cartasJugadas.get(cartasJugadas.size() - 1))) {
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
            CartaUNO robada = baraja.getCartas().remove(0);
            jugador.getMano().add(robada);
            System.out.println("Robó: " + robada);
        }
    }

    private void jugarCarta(Jugador jugador, CartaUNO carta) {
        cartasJugadas.add(carta);
        jugador.getMano().remove(carta);
        jugador.setUltimaRobada(null);
        System.out.println(jugador.getNombre() + " jugó: " + carta);

        // Aplicar efectos especiales
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

    private boolean aplicarNegarTurno() {
        System.out.println("¡El siguiente jugador pierde su turno!");
        siguienteTurno();
        return true;
    }

    private void aplicarCambioSentido() {
        direccionJuego *= -1;  // Cambia la dirección del turno
        System.out.println("¡El sentido del juego ha cambiado!");
    }

    private boolean aplicarMas2() {
        Jugador siguiente = obtenerSiguienteJugador();
        robarCartas(siguiente, 2);
        System.out.println("¡" + siguiente.getNombre() + " roba 2 cartas!");
        siguienteTurno();
        return true;
    }

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

        int numero = cartasJugadas.get(cartasJugadas.size() - 1).getNumero();
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
    }

    public static void main(String[] args) {
        new TableroUNO();
    }
}
