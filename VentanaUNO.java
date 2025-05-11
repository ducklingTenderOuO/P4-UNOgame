/**
 * Write a description of class VentanaUNO here.
 * Clase para visualizar la ronda del juego UNO
 * @author (AYJB)
 * @version (MAY 2025)
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class VentanaUNO extends JFrame {
    private JPanel panelCartas;
    private JLabel cartaEnMesa;
    private JLabel titulo;
    private CartaUNO cartaSeleccionada;

    public VentanaUNO() {
        setTitle("UNO GAMEEEEEEE");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Dejo un espacio vacio en la parte de arriba
        JPanel panelVacio = new JPanel();
        panelVacio.setPreferredSize(new Dimension(1000, 10));
        add(panelVacio, BorderLayout.NORTH);

        // La carta acutal
        cartaEnMesa = new JLabel("Carta actual", JLabel.CENTER);
        cartaEnMesa.setFont(new Font("Arial", Font.BOLD, 22));
        cartaEnMesa.setHorizontalAlignment(SwingConstants.CENTER);
        cartaEnMesa.setPreferredSize(new Dimension(100, 150));
        add(cartaEnMesa, BorderLayout.CENTER);

        // Tu mano
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));

        titulo = new JLabel("", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelCartas.setPreferredSize(new Dimension(800, 180)); // Espacio para las cartas

        panelInferior.add(titulo);
        panelInferior.add(panelCartas);

        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    public CartaUNO getCartaSeleccionada() {
        return cartaSeleccionada;
    }

    public void setCartaSeleccionada(CartaUNO carta) {
        this.cartaSeleccionada = carta;
    }

    public void actualizarTitulo(String nombreJugador) {
        titulo.setText("Mano de " + nombreJugador);
    }

    public void mostrarCartaActual(CartaUNO carta) {
        if (carta == null) {
            // Si no hay carta, limpiamos la imagen y mostramos un mensaje equis
            cartaEnMesa.setIcon(null);
            cartaEnMesa.setText("¡Juego terminado!");
            return;
        }

        // Mostrar imagen de la carta
        ImageIcon icono = new ImageIcon(carta.getRutaImagen());
        Image imagenEscalada = icono.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        cartaEnMesa.setIcon(new ImageIcon(imagenEscalada));
        cartaEnMesa.setText(""); // Limpiar texto en caso de que tuviera algo antes
    }

    public void mostrarCartas(ArrayList<CartaUNO> mano) {
        panelCartas.removeAll(); // Limpiar cartas anteriores

        for (CartaUNO carta : mano) {
            try {
                BufferedImage image = ImageIO.read(new File(carta.getRutaImagen()));
                Image imagenEscalada = image.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                JButton boton = new JButton(new ImageIcon(imagenEscalada));
                boton.setPreferredSize(new Dimension(100, 150));
                boton.setToolTipText(carta.toString());

                // Acción al hacer clic
                boton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        cartaSeleccionada = carta;
                        System.out.println("Carta seleccionada: " + carta);
                    }
                });

                panelCartas.add(boton);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + carta.getRutaImagen());
                e.printStackTrace();
            }
        }

        panelCartas.revalidate();
        panelCartas.repaint();
    }
}
