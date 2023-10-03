package IU;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import static IU.EstadoCasilla.JUGADOR;
import static IU.EstadoCasilla.OCUPADA;
import static java.awt.Color.BLACK;

/**
 * @author Victor Manuel Blanes Castro
 */
public class Casilla {

    private Rectangle2D.Float rectangle;
    private Color color;
    private EstadoCasilla objetoEnCasilla;

    public Casilla(Rectangle2D.Float rectangle, Color color, EstadoCasilla objetoEnCasilla) {
        this.rectangle = rectangle;
        this.color = color;
        this.objetoEnCasilla = objetoEnCasilla;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getFillColor());
        g2d.fill(this.rectangle);
        if (this.objetoEnCasilla == JUGADOR) {
            Ellipse2D.Float figuraJugador = new Ellipse2D.Float(this.rectangle.x, this.rectangle.y,
                    this.rectangle.width, this.rectangle.height);
            g2d.setColor(BLACK);
            g2d.fill(figuraJugador);
        }
    }

    private Color getFillColor() {
        return (this.objetoEnCasilla == OCUPADA) ? BLACK : this.color;
    }

    public Rectangle2D.Float getRectangle() {
        return rectangle;
    }

    public EstadoCasilla getObjetoEnCasilla() {
        return objetoEnCasilla;
    }

    public void setObjetoEnCasilla(EstadoCasilla objetoEnCasilla) {
        this.objetoEnCasilla = objetoEnCasilla;
    }


}
