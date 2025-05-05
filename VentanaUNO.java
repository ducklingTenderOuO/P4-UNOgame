import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VentanaUNO extends JFrame {
    private JPanel panelCartas;
    private JLabel cartaEnMesa;
    private JLabel titulo;

    public VentanaUNO() {
        setTitle("UNO GAMEEEEEEE");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === PARTE SUPERIOR (espacio vacío para separar visualmente) ===
        JPanel panelVacio = new JPanel();
        panelVacio.setPreferredSize(new Dimension(1000, 10)); // Un pequeño espacio vacío
        add(panelVacio, BorderLayout.NORTH);

        // === PARTE CENTRAL: Carta actual ===
        cartaEnMesa = new JLabel("Carta actual", JLabel.CENTER);
        cartaEnMesa.setFont(new Font("Arial", Font.BOLD, 22));
        cartaEnMesa.setHorizontalAlignment(SwingConstants.CENTER);
        cartaEnMesa.setPreferredSize(new Dimension(100, 150));  // Tamaño estándar
        add(cartaEnMesa, BorderLayout.CENTER);

        // === PARTE INFERIOR: "Tu mano" + cartas ===
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));

        titulo = new JLabel("", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado del texto

        panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centrado horizontal
        panelCartas.setPreferredSize(new Dimension(800, 180)); // Espacio para las cartas

        panelInferior.add(titulo);
        panelInferior.add(panelCartas);

        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actualizarTitulo(String nombreJugador) {
        titulo.setText("Mano de " + nombreJugador);
    }

    public void mostrarCartaActual(CartaUNO carta) {
        ImageIcon icono = new ImageIcon(carta.getRutaImagen());
        Image imagenEscalada = icono.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        cartaEnMesa.setIcon(new ImageIcon(imagenEscalada));
        cartaEnMesa.setText(""); // Eliminar texto si tenía
    }

    public void mostrarCartas(ArrayList<CartaUNO> mano) {
        panelCartas.removeAll(); // Limpiar cartas anteriores

        for (CartaUNO carta : mano) {
            ImageIcon icono = new ImageIcon(carta.getRutaImagen());
            Image imagenEscalada = icono.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
            JLabel etiqueta = new JLabel(new ImageIcon(imagenEscalada));
            etiqueta.setToolTipText(carta.toString());
            panelCartas.add(etiqueta);
        }

        panelCartas.revalidate();
        panelCartas.repaint();
    }
}