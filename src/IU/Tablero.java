package IU;

import pasadizoestrecho.CasillasAdyacentes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static IU.EstadoCasilla.JUGADOR;
import static IU.EstadoCasilla.NADA;
import static IU.EstadoCasilla.OCUPADA;
import static java.util.Objects.isNull;
import static pasadizoestrecho.CasillasAdyacentes.*;

/**
 * @author Victor Manuel Blanes Castro
 */
public class Tablero extends JPanel {

    public static int CASILLAS_POR_LADO = 30;
    private static final int DIMENSION_TABLERO_PX = 780;
    private static int DIMENSION_CASILLA_PX = DIMENSION_TABLERO_PX / CASILLAS_POR_LADO;
    private static final Color BLANCO = new Color(242, 242, 242);
    private static final Color NEGRO = new Color(230, 230, 230);
    private Casilla[][] tablero;

    //TODO: A lo mejor ver de usar optionals
    //TODO: Mirar de cambiar lo de coordenadas para no crear tantos nuevos objetos

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
                tablero[pos_x][pos_y] = new Casilla(rectanguloCasilla, colorCasilla, isOnMapLimit(new Coordenada(pos_x, pos_y)) ? OCUPADA : NADA);
            }
        }
        this.tablero = tablero;
    }

    private boolean isOnMapLimit(Coordenada coordenada) {
        return (coordenada.X == 0 || coordenada.Y == 0 || coordenada.X == (CASILLAS_POR_LADO - 1) || coordenada.Y == (CASILLAS_POR_LADO - 1));
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

    public boolean isPixelOnCasilla(int pos_x, int pos_y, int pixel_x, int pixel_y) {
        return tablero[pos_x][pos_y].getRectangle().contains(pixel_x, pixel_y);
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

    private Casilla getNearCasilla(Coordenada coordenada, CasillasAdyacentes posicionVecina) {
        //Norte va para abajo y Sur para arriba debido a como java pinta la pantalla del tablero
        return switch (posicionVecina) {
            case NORTE ->
                    checkIfInBounds(new Coordenada(coordenada.X, coordenada.Y - 1)) ? tablero[coordenada.X][coordenada.Y - 1] : null;
            case SUR ->
                    checkIfInBounds(new Coordenada(coordenada.X, coordenada.Y + 1)) ? tablero[coordenada.X][coordenada.Y + 1] : null;
            case ESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X + 1, coordenada.Y)) ? tablero[coordenada.X + 1][coordenada.Y] : null;
            case OESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X - 1, coordenada.Y)) ? tablero[coordenada.X - 1][coordenada.Y] : null;
            case NORESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X + 1, coordenada.Y - 1)) ? tablero[coordenada.X + 1][coordenada.Y - 1] : null;
            case NOROESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X - 1, coordenada.Y - 1)) ? tablero[coordenada.X - 1][coordenada.Y - 1] : null;
            case SURESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X + 1, coordenada.Y + 1)) ? tablero[coordenada.X + 1][coordenada.Y + 1] : null;
            case SUROESTE ->
                    checkIfInBounds(new Coordenada(coordenada.X - 1, coordenada.Y + 1)) ? tablero[coordenada.X - 1][coordenada.Y + 1] : null;
        };
    }

    private boolean checkIfInBounds(Coordenada coordenada) {
        return (coordenada.X >= 0 || coordenada.Y >= 0 || coordenada.X <= (CASILLAS_POR_LADO - 1) || coordenada.Y <= (CASILLAS_POR_LADO - 1));
    }

    public void moverPlayer(CasillasAdyacentes accion) throws CustomException {
        Coordenada coordinates = retrieveCasillaWithPlayer();
        if (isNull(coordinates)) {
            throw new CustomException(String.format("Error: Se ha intentado mover %s al jugador, pero no hay jugador en el tablero", accion));
        }

        tablero[coordinates.X][coordinates.Y].setObjetoEnCasilla(NADA);
        Casilla casilla = getNearCasilla(coordinates, accion);
        if (isNull(casilla)) {
            throw new CustomException(String.format("Error: Se ha intentado acceder a una posicion no valida: [%d, %d] moviendo %s",
                    coordinates.X, coordinates.Y, accion));
        }
        if (casilla.getObjetoEnCasilla() == OCUPADA) {
            throw new CustomException(String.format("Error: El jugador ha intentado moverse %s a una casilla ocupada, casilla actual [%d, %d]",
                    accion, coordinates.X, coordinates.Y));
        }
        casilla.setObjetoEnCasilla(JUGADOR);
    }

    public boolean[] setPercepciones() {
        Coordenada jugador = retrieveCasillaWithPlayer();
        boolean[] percepciones = new boolean[8];
        percepciones[0] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, NOROESTE)) && getNearCasilla(jugador, NOROESTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[1] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, NORTE)) && getNearCasilla(jugador, NORTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[2] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, NORESTE)) && getNearCasilla(jugador, NORESTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[3] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, ESTE)) && getNearCasilla(jugador, ESTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[4] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, SURESTE)) && getNearCasilla(jugador, SURESTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[5] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, SUR)) && getNearCasilla(jugador, SUR).getObjetoEnCasilla() == OCUPADA);
        percepciones[6] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, SUROESTE)) && getNearCasilla(jugador, SUROESTE).getObjetoEnCasilla() == OCUPADA);
        percepciones[7] = (!isNull(jugador) && !isNull(getNearCasilla(jugador, OESTE)) && getNearCasilla(jugador, OESTE).getObjetoEnCasilla() == OCUPADA);
        return percepciones;
    }
}
