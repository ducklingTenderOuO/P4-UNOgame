import java.util.ArrayList;
import java.util.Collections;

public class BarajaDeUNO {
    private final ArrayList<CartaUNO> cartas;

    public BarajaDeUNO() {
        cartas = new ArrayList<>();
        llenar();
        mezclarCartas();
    }

    private void llenar() {
        for (String color : CartaUNO.COLORES) {
            if (!color.equals("negro")) {
                cartas.add(new CartaUNO(color, 0)); // 1 carta 0 por color

                for (int i = 1; i <= 12; i++) { // 2 cartas del 1-12 por color
                    cartas.add(new CartaUNO(color, i));
                    cartas.add(new CartaUNO(color, i));
                }
            }
        }

        // Cartas especiales negras (4 de cada)
        for (int i = 0; i < 4; i++) {
            cartas.add(new CartaUNO("negro", 13)); // Comodín cambio de color
            cartas.add(new CartaUNO("negro", 14)); // Comodín +4
        }
    }

    private void mezclarCartas() {
        Collections.shuffle(cartas);
    }

    public ArrayList<CartaUNO> getCartas() {
        return cartas;
    }
}