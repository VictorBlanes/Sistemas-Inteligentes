package pasadizoestrecho;

import IU.Tablero;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Victor Manuel Blanes Castro
 */
public class PasadizoEstrecho extends JFrame implements MouseListener, KeyListener {

    private static Tablero tablero;
    private static boolean autoEnabled = false;
    private static Agente robot = new Agente();
    private static JSpinner campoDimension;
    private static JPanel setDimension, subSetDimension, subEjecucion, ejecucion;
    private static JButton step, auto;
    private static JLabel modDimension, ejMode;
    private static JPanel opContainer;

    public PasadizoEstrecho() {
        super("Agente reactivo practica");
        modDimension = new JLabel("Modificar dimension");
        ejMode = new JLabel("Ejecutar programa");
        ejecucion = new JPanel();
        subSetDimension = new JPanel();
        subEjecucion = new JPanel();
        opContainer = new JPanel();
        setDimension = new JPanel();
        campoDimension = new JSpinner(new SpinnerNumberModel(30, 5, 50, 1));
        step = new JButton("Step");
        auto = new JButton("Auto");
        addComponents();
    }

    private void addComponents() {
        //Modificar Dimension IU
        setDimension.setLayout(new BoxLayout(setDimension, BoxLayout.Y_AXIS));
        modDimension.setAlignmentX(Component.CENTER_ALIGNMENT);
        setDimension.add(modDimension);

        subSetDimension.setLayout(new BoxLayout(subSetDimension, BoxLayout.X_AXIS));
        campoDimension.setAlignmentY(Component.CENTER_ALIGNMENT);
        subSetDimension.add(campoDimension);

        //Ejecutar Programa IU
        subEjecucion.setLayout(new BoxLayout(subEjecucion, BoxLayout.X_AXIS));
        subEjecucion.add(step);
        subEjecucion.add(auto);

        ejecucion.setLayout(new BoxLayout(ejecucion, BoxLayout.Y_AXIS));
        subEjecucion.setAlignmentX(Component.LEFT_ALIGNMENT);
        ejMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        ejecucion.add(ejMode);
        ejecucion.add(subEjecucion);

        setDimension.add(subSetDimension);

        //JPanel que reune todos los JPanel anteriores
        opContainer.setLayout(new BoxLayout(opContainer, BoxLayout.Y_AXIS));
        setDimension.setAlignmentX(Component.LEFT_ALIGNMENT);
        ejecucion.setAlignmentX(Component.LEFT_ALIGNMENT);
        opContainer.add(Box.createRigidArea(new Dimension(10, 30)));
        opContainer.add(setDimension);
        opContainer.add(Box.createRigidArea(new Dimension(10, 360)));
        opContainer.add(ejecucion);

        tablero = new Tablero();
        tablero.addMouseListener(this);

        campoDimension.addChangeListener((ChangeEvent evt) -> {
            tablero.resizeArray((int) campoDimension.getValue());
            repaint();
        });
        step.addActionListener((ActionEvent evt) -> {
            robot.nextAccion();
        });
        auto.addActionListener((ActionEvent evt) -> {
            autoEnabled = !autoEnabled;
            campoDimension.setEnabled(!autoEnabled);
            step.setEnabled(!autoEnabled);
            auto.setText(autoEnabled ? "Stop" : "Auto");
            robot.startStop();
        });

        this.setLayout(new FlowLayout());
        this.getContentPane().add(opContainer);
        this.getContentPane().add(tablero);
        this.setSize(tablero.getPreferredSize());
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println(e.getButton());
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!autoEnabled) {
            int x = e.getX(), y = e.getY();
            int i = 0, j = 0;
            boolean encontrado = false;
            //Busca la casilla que ha clickado el usuario
            for (i = 0; i < Tablero.CASILLAS_POR_LADO && !encontrado; i++) {
                for (j = 0; j < Tablero.CASILLAS_POR_LADO && !encontrado; j++) {
                    encontrado = tablero.esCasilla(i, j, x, y);
                }
            }
            --i;
            --j;
            // El primer if evita que el usuario pueda eliminar los bordes del mapa
            if (!(i == 0 || j == 0 || i == (Tablero.CASILLAS_POR_LADO - 1) || j == (Tablero.CASILLAS_POR_LADO - 1))) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    tablero.ocuparLiberarCasilla(i, j);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    tablero.setPlayer(i, j);
                }
            }
            tablero.repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void startRobot() {
        robot.efecAccion(tablero);
    }
    public static void main(String[] args) {
        PasadizoEstrecho pe = new PasadizoEstrecho();
        pe.setVisible(true);
        startRobot();
    }
}
