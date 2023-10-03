package IU;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Victor Manuel Blanes Castro
 */
public class Casilla {
       
    private Rectangle2D.Float rec;
    private Color col;
    private EstadoCasilla casilla;

    public Casilla(Rectangle2D.Float r, Color c, EstadoCasilla ocu ) {
        this.rec = r;
        this.col = c;
        this.casilla = ocu;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(this.casilla.equals(EstadoCasilla.OCUPADA)){
            g2d.setColor(Color.BLACK);
        }else{
            g2d.setColor(this.col);
        }
        g2d.fill(this.rec);
        if(this.casilla.equals(EstadoCasilla.JUGADOR)){
            Ellipse2D.Float eli = new Ellipse2D.Float(this.rec.x, this.rec.y, 
                    this.rec.width, this.rec.height);
            g2d.setColor(Color.BLACK);
            g2d.fill(eli);
        }
    }

    public Rectangle2D.Float getRec() {
        return rec;
    }

    public EstadoCasilla getCasilla() {
        return casilla;
    }

    public void setCasilla(EstadoCasilla casilla) {
        this.casilla = casilla;
    }

  

}
