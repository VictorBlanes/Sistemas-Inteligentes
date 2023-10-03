package IU;

import pasadizoestrecho.ConjuntoAcciones;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Victor Manuel Blanes Castro
 */
public class Tablero extends JPanel {

    public static int DIMENSION = 30;

    private static final int MAXIMO = 780;
    private static int LADO = MAXIMO / DIMENSION;
    private static final Color BLANCO = new Color(242, 242, 242);
    private static final Color NEGRO = new Color(230, 230, 230);
    private Casilla t[][];
    private boolean jugadorEnMapa = false;

    public Tablero() {
        t = new Casilla[DIMENSION][DIMENSION];
        int y = 0;
        for (int i = 0; i < DIMENSION; i++) {
            int x = 0;
            for (int j = 0; j < DIMENSION; j++) {
                Rectangle2D.Float r = new Rectangle2D.Float(x, y, LADO, LADO);
                Color col;
                if ((i % 2 == 1 && j % 2 == 1) || (i % 2 == 0 && j % 2 == 0)) {
                    col = BLANCO;
                } else {
                    col = NEGRO;
                }
                if(i == 0 || j == 0 || i == (DIMENSION - 1) || j == (DIMENSION - 1)){
                    t[i][j] = new Casilla(r, col, EstadoCasilla.OCUPADA);
                }else{
                    t[i][j] = new Casilla(r, col, EstadoCasilla.NADA);
                }
                x += LADO;
            }
            y += LADO;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                t[i][j].paintComponent(g);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MAXIMO, MAXIMO);
    }

    public boolean esCasilla(int i, int j, int x, int y) {
        return t[i][j].getRec().contains(x, y);
    }

    public EstadoCasilla estaOcupado(int i, int j) {
        return t[i][j].getCasilla();
    }

    public void ocuparDesocupar(int i, int j) {
        if (t[i][j].getCasilla().equals(EstadoCasilla.NADA)) {
            t[i][j].setCasilla(EstadoCasilla.OCUPADA);
        } else if (t[i][j].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            t[i][j].setCasilla(EstadoCasilla.NADA);
        }
    }

    public void setPlayer(int i, int j) {
        if (t[i][j].getCasilla().equals(EstadoCasilla.NADA) && !jugadorEnMapa) {
            t[i][j].setCasilla(EstadoCasilla.JUGADOR);
            jugadorEnMapa = true;
        } else if (t[i][j].getCasilla().equals(EstadoCasilla.JUGADOR)) {
            t[i][j].setCasilla(EstadoCasilla.NADA);
            jugadorEnMapa = false;
        }
    }

    public void moverPlayer(ConjuntoAcciones accion) {
        boolean encontrado = false;
        int i = 0, j = 0;
        for (i = 0; i < Tablero.DIMENSION && !encontrado; i++) {
            for (j = 0; j < Tablero.DIMENSION && !encontrado; j++) {
                encontrado
                        = t[i][j].getCasilla().equals(EstadoCasilla.JUGADOR);
            }
        }
        t[--i][--j].setCasilla(EstadoCasilla.NADA);
        if (encontrado) {
            switch (accion) {
                case NORTE ->
                    t[--i][j].setCasilla(EstadoCasilla.JUGADOR);
                case SUR ->
                    t[++i][j].setCasilla(EstadoCasilla.JUGADOR);
                case ESTE ->
                    t[i][++j].setCasilla(EstadoCasilla.JUGADOR);
                case OESTE ->
                    t[i][--j].setCasilla(EstadoCasilla.JUGADOR);
            }
        }
    }
    
    public void resizeArray(int dim) {
        DIMENSION = dim;
        LADO = MAXIMO / DIMENSION;
        boolean player = false;
        Casilla t2[][] = new Casilla[DIMENSION][DIMENSION];
        int y = 0;
        for (int i = 0; i < DIMENSION; i++) {
            int x = 0;
            for (int j = 0; j < DIMENSION; j++) {
                Rectangle2D.Float r = new Rectangle2D.Float(x, y, LADO, LADO);
                Color col;
                if ((i % 2 == 1 && j % 2 == 1) || (i % 2 == 0 && j % 2 == 0)) {
                    col = BLANCO;
                } else {
                    col = NEGRO;
                }
                if(i == 0 || j == 0 || i == (DIMENSION - 1) || j == (DIMENSION - 1)){
                    t2[i][j] = new Casilla(r, col, EstadoCasilla.OCUPADA);
                }else{
                    t2[i][j] = new Casilla(r, col, EstadoCasilla.NADA);
                }
                x += LADO;
            }
            y += LADO;
        }
        t = t2;
        jugadorEnMapa = player;
    }

    public Rectangle getRectangle(int i, int j) {
        return t[i][j].getRec().getBounds();
    }

    public boolean[] setPercepciones() {
        int i = 0, j = 0;
        boolean encontrado = false;
        for (i = 0; i < Tablero.DIMENSION && !encontrado; i++) {
            for (j = 0; j < Tablero.DIMENSION && !encontrado; j++) {
                encontrado
                        = t[i][j].getCasilla().equals(EstadoCasilla.JUGADOR);
            }
        }
        i--;
        j--;
        boolean[] percepciones = new boolean[8];
        if (i > 0 && j > 0 && encontrado
                && t[i - 1][j - 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[0] = true;
        } //S1
        if (i > 0 && encontrado
                && t[i - 1][j].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[1] = true;
        } //S2
        if (j < (DIMENSION - 1) && i > 0 && encontrado
                && t[i - 1][j + 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[2] = true;
        } //S3
        if (j < (DIMENSION - 1) && encontrado
                && t[i][j + 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[3] = true;
        } //S4
        if (i < (DIMENSION - 1) && j < (DIMENSION - 1) && encontrado
                && t[i + 1][j + 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[4] = true;
        } //S5
        if (i < (DIMENSION - 1) && encontrado
                && t[i + 1][j].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[5] = true;
        } //S6
        if (j > 0 && i < (DIMENSION - 1) && encontrado
                && t[i + 1][j - 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[6] = true;
        } //S7
        if (j > 0 && encontrado
                && t[i][j - 1].getCasilla().equals(EstadoCasilla.OCUPADA)) {
            percepciones[7] = true;
        } //S8
        return percepciones;
    }
}
