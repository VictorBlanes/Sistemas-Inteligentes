package IU;

import pasadizoestrecho.ConjuntoAcciones;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static IU.EstadoCasilla.JUGADOR;
import static IU.EstadoCasilla.NADA;
import static IU.EstadoCasilla.OCUPADA;
import static java.util.Objects.isNull;

/**
 * @author Victor Manuel Blanes Castro
 */
public class Tablero extends JPanel {

    public static int CASILLAS_POR_LADO = 30;
    private static final int DIMENSION_TABLERO_PX = 780;
    private static int DIMENSION_CASILLA_PX = DIMENSION_TABLERO_PX / CASILLAS_POR_LADO;
    private static final Color BLANCO = new Color(242, 242, 242);
    private static final Color NEGRO = new Color(230, 230, 230);
    private Casilla tablero[][];

    //TODO: Ver como juntar isOnMapLimit y checkIfInBounds
    //TODO: Ver de cambiar tablero de un array a una lista

    public Tablero() {
        initializeTablero(CASILLAS_POR_LADO);
    }

    public void resizeArray(int dim) {
        CASILLAS_POR_LADO = dim;
        DIMENSION_CASILLA_PX = DIMENSION_TABLERO_PX / CASILLAS_POR_LADO;
        initializeTablero(dim);
    }

    private void initializeTablero(int dimension) {
        Color colorCasilla;
        Casilla[][] tablero = new Casilla[dimension][dimension];
        for (int pos_x = 0; pos_x < dimension; pos_x++) {
            for (int pos_y = 0; pos_y < dimension; pos_y++) {
                Rectangle2D.Float rectanguloCasilla = new Rectangle2D.Float(
                        pos_x * DIMENSION_CASILLA_PX,
                        pos_y * DIMENSION_CASILLA_PX,
                        DIMENSION_CASILLA_PX,
                        DIMENSION_CASILLA_PX);
                colorCasilla = (pos_x % 2 == pos_y % 2) ? BLANCO : NEGRO;
                tablero[pos_x][pos_y] = new Casilla(rectanguloCasilla, colorCasilla, isOnMapLimit(pos_x, pos_y) ? OCUPADA : NADA);
            }
        }
        this.tablero = tablero;
    }

    private boolean isOnMapLimit(int pos_x, int pos_y) {
        return (pos_x == 0 || pos_y == 0 || pos_x == (CASILLAS_POR_LADO - 1) || pos_y == (CASILLAS_POR_LADO - 1));
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int pos_x = 0; pos_x < CASILLAS_POR_LADO; pos_x++) {
            for (int pos_y = 0; pos_y < CASILLAS_POR_LADO; pos_y++) {
                tablero[pos_x][pos_y].paintComponent(g);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DIMENSION_TABLERO_PX, DIMENSION_TABLERO_PX);
    }

    public boolean isPixelOnCasilla(int pos_x, int pos_y, int pixel_coord_x, int pixel_coord_y) {
        return tablero[pos_x][pos_y].getRectangle().contains(pixel_coord_x, pixel_coord_y);
    }

    public void ocuparLiberarCasilla(int pos_x, int pos_y) {
        if (tablero[pos_x][pos_y].getObjetoEnCasilla() == NADA) {
            tablero[pos_x][pos_y].setObjetoEnCasilla(OCUPADA);
        } else if (tablero[pos_x][pos_y].getObjetoEnCasilla() == OCUPADA) {
            tablero[pos_x][pos_y].setObjetoEnCasilla(NADA);
        }
    }

    public void setPlayer(int pos_x, int pos_y) {
        Coordenada coordenadaOfPlayer = retrieveCasillaWithPlayer();
        if (isNull(coordenadaOfPlayer)) {
            tablero[pos_x][pos_y].setObjetoEnCasilla(JUGADOR);
        } else {
            tablero[coordenadaOfPlayer.X][coordenadaOfPlayer.Y].setObjetoEnCasilla(NADA);
            tablero[pos_x][pos_y].setObjetoEnCasilla(JUGADOR);
        }
    }

    private Coordenada retrieveCasillaWithPlayer() {
        for (int pos_x = 0; pos_x < CASILLAS_POR_LADO; pos_x++) {
            for (int pos_y = 0; pos_y < CASILLAS_POR_LADO; pos_y++) {
                if (tablero[pos_x][pos_y].getObjetoEnCasilla() == JUGADOR) {
                    return new Coordenada(pos_x, pos_y);
                }
            }
        }
        return null;
    }

    private Casilla getNearCasilla(Coordenada coordenada, ConjuntoAcciones posicionVecina) throws CustomException {
        switch (posicionVecina) {
            case NORTE:
                checkIfInBounds(new Coordenada(coordenada.Y, coordenada.Y + 1));
                return tablero[coordenada.X][coordenada.X + 1];
            case SUR:
                checkIfInBounds(new Coordenada(coordenada.X, coordenada.Y - 1));
                return tablero[coordenada.X][coordenada.X - 1];
            case ESTE:
                checkIfInBounds(new Coordenada(coordenada.X + 1, coordenada.Y));
                return tablero[coordenada.X + 1][coordenada.Y];
            default:
            case OESTE:
                checkIfInBounds(new Coordenada(coordenada.X - 1, coordenada.Y));
                return tablero[coordenada.X - 1][coordenada.Y];
        }
    }

    private void checkIfInBounds(Coordenada coordenada) throws CustomException {
        if (coordenada.X < 0 || coordenada.Y < 0) {
            throw new CustomException(String.format("Error: Se ha intentado acceder a una posicion no valida: [%d, %d]",
                    coordenada.X, coordenada.Y));
        }

        if (coordenada.X > CASILLAS_POR_LADO - 1 || coordenada.Y > CASILLAS_POR_LADO - 1) {
            throw new CustomException(String.format("Error: Se ha intentado acceder a una posicion no valida: [%d, %d]",
                    coordenada.X, coordenada.Y));
        }
    }

    public void moverPlayer(ConjuntoAcciones accion) throws CustomException {
        Coordenada coordinates = retrieveCasillaWithPlayer();
        if (isNull(coordinates)) {
            throw new CustomException(String.format("Error: Se ha intentado mover %s al jugador, pero no hay jugador en el tablero", accion));
        }

        tablero[coordinates.X][coordinates.Y].setObjetoEnCasilla(NADA);
        Casilla casilla = getNearCasilla(coordinates, accion);
        if (casilla.getObjetoEnCasilla() == OCUPADA) {
            throw new CustomException(String.format("Error: El jugador ha intentado moverse %s a una casilla ocupada, casilla actual [%d, %d]",
                    accion, coordinates.X, coordinates.Y));
        }
        casilla.setObjetoEnCasilla(JUGADOR);
    }

    public boolean[] setPercepciones() {
        int i = 0, j = 0;
        boolean encontrado = false;
        for (i = 0; i < Tablero.CASILLAS_POR_LADO && !encontrado; i++) {
            for (j = 0; j < Tablero.CASILLAS_POR_LADO && !encontrado; j++) {
                encontrado
                        = tablero[i][j].getObjetoEnCasilla().equals(JUGADOR);
            }
        }
        i--;
        j--;
        boolean[] percepciones = new boolean[8];
        if (i > 0 && j > 0 && encontrado
                && tablero[i - 1][j - 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[0] = true;
        } //S1
        if (i > 0 && encontrado
                && tablero[i - 1][j].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[1] = true;
        } //S2
        if (j < (CASILLAS_POR_LADO - 1) && i > 0 && encontrado
                && tablero[i - 1][j + 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[2] = true;
        } //S3
        if (j < (CASILLAS_POR_LADO - 1) && encontrado
                && tablero[i][j + 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[3] = true;
        } //S4
        if (i < (CASILLAS_POR_LADO - 1) && j < (CASILLAS_POR_LADO - 1) && encontrado
                && tablero[i + 1][j + 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[4] = true;
        } //S5
        if (i < (CASILLAS_POR_LADO - 1) && encontrado
                && tablero[i + 1][j].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[5] = true;
        } //S6
        if (j > 0 && i < (CASILLAS_POR_LADO - 1) && encontrado
                && tablero[i + 1][j - 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[6] = true;
        } //S7
        if (j > 0 && encontrado
                && tablero[i][j - 1].getObjetoEnCasilla().equals(OCUPADA)) {
            percepciones[7] = true;
        } //S8
        return percepciones;
    }
}
