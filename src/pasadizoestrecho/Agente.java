package pasadizoestrecho;

import IU.CustomException;
import IU.Tablero;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Victor Manuel Blanes Castro
 */
public class Agente {
    private final int NOROESTE = 0;
    private final int NORTE = 1;
    private final int NORESTE = 2;
    private final int ESTE = 3;
    private final int SURESTE = 4;
    private final int SUR = 5;
    private final int SUROESTE = 6;
    private final int OESTE = 7;
    private final int V_NORTE = 0;
    private final int V_ESTE = 1;
    private final int V_SUR = 2;
    private final int V_OESTE = 3;
    private final int V_INTERCARDINALES = 4;
    private final int V_CARDINALES = 5;
    private boolean[] percepciones = new boolean[8];
    private boolean[] vecCaracteristicas = new boolean[6];
    private boolean[] lastVecCaracteristicas = new boolean[6];
    private int SPEED = 100;
    private CasillasAdyacentes lastAccion;
    private static Semaphore mutex = new Semaphore(0); //Barrera
    private static boolean auto = false; //Control de movimiento automatico del robot

    public Agente() {
    }

    public boolean[] percibir(Tablero tbl) {
        percepciones = tbl.setPercepciones();
        return percepciones;
    }

    /* actVecCaracteristicas
        A partir de las percepciones del agente actualiza el vector de caractersiticas.
     */
    public boolean[] actVecCaracteristicas() {
        vecCaracteristicas = new boolean[6];
        if (percepciones[NORTE]) { //X1 - Norte
            vecCaracteristicas[V_NORTE] = true;
        }
        if (percepciones[ESTE]) { //X2 - Este
            vecCaracteristicas[V_ESTE] = true;
        }
        if (percepciones[SUR]) { //X3 - Sur
            vecCaracteristicas[V_SUR] = true;
        }
        if (percepciones[OESTE]) { //X4 - Oeste
            vecCaracteristicas[V_OESTE] = true;
        }
        if (percepciones[NOROESTE] || percepciones[NORESTE] //X5 - Pattern 'x'
                || percepciones[SURESTE] || percepciones[SUROESTE]) {
            vecCaracteristicas[V_INTERCARDINALES] = true;
        }
        if (percepciones[NORTE] || percepciones[ESTE] //X6 - Pattern '+'
                || percepciones[SUR] || percepciones[OESTE]) {
            vecCaracteristicas[V_CARDINALES] = true;
        }
        return vecCaracteristicas;
    }

    /* efecAccion
        A partir del vector de caracteristicas y la accion anterior elige 
        que accion tomar.
     */
    public void efecAccion(Tablero tbl) throws CustomException {
        while (true) {
            CasillasAdyacentes accion;
            percibir(tbl);
            actVecCaracteristicas();
            try {
                if (auto) {
                    Thread.sleep(SPEED);
                } else {
                    mutex.acquire();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
            }
            accion = elegirAccion();
            tbl.moverPlayer(accion);
            lastVecCaracteristicas = vecCaracteristicas;
            lastAccion = accion;
            tbl.repaint();
        }
    }

    private CasillasAdyacentes elegirAccion() {
        CasillasAdyacentes accion = null;
        if (lastVecCaracteristicas[V_CARDINALES]) {
            // Por defecto intenta continuar en la misma direccion que la anterior,
            // si no puede va comprobando si puede rotar en sentido antihorario
            accion = elegirAccionSeguirParedORotar();
        } else if (lastVecCaracteristicas[V_INTERCARDINALES] && vecCaracteristicas[V_CARDINALES]) {
            // "Se encarga de la accion posterior a una rotacion"
            // Reevalua la direccion que tiene que seguir en el caso posterior a un cambio de direccion
            // Por defecto intenta continuar en la misma direccion que habia tomado
            // Si no puede, intenta girar en sentido antihorario
            accion = elegirAccionPostRotacion();
        } else if (vecCaracteristicas[V_CARDINALES]) {
            // "Se encarga de empezar a seguir una pared cuando encuentra una"
            if (vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.SUR;
            } else if (vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else if (vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.ESTE;
            }
        } else {
            // Accion por defecto
            accion = CasillasAdyacentes.NORTE;
        }
        return accion;
    }

    private CasillasAdyacentes elegirAccionSeguirParedORotar() {
        CasillasAdyacentes accion = null;
        if (lastAccion == CasillasAdyacentes.ESTE) {
            if (!vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else if (!vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.ESTE;
            } else if (!vecCaracteristicas[V_SUR]) {
                accion = CasillasAdyacentes.SUR;
            } else {
                accion = CasillasAdyacentes.OESTE;
            }
        } else if (lastAccion == CasillasAdyacentes.SUR) {
            if (!vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.ESTE;
            } else if (!vecCaracteristicas[V_SUR]) {
                accion = CasillasAdyacentes.SUR;
            } else if (!vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.OESTE;
            } else {
                accion = CasillasAdyacentes.NORTE;
            }
        } else if (lastAccion == CasillasAdyacentes.OESTE) {
            if (!vecCaracteristicas[V_SUR]) {
                accion = CasillasAdyacentes.SUR;
            } else if (!vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.OESTE;
            } else if (!vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else {
                accion = CasillasAdyacentes.ESTE;
            }
        } else if (lastAccion == CasillasAdyacentes.NORTE) {
            if (!vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.OESTE;
            } else if (!vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else if (!vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.ESTE;
            } else {
                accion = CasillasAdyacentes.SUR;
            }
        }
        return accion;
    }

    private CasillasAdyacentes elegirAccionPostRotacion() {
        CasillasAdyacentes accion = null;
        if (lastAccion == CasillasAdyacentes.ESTE) {
            if (!vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.ESTE;
            } else if (!vecCaracteristicas[V_SUR]) {
                accion = CasillasAdyacentes.SUR;
            } else {
                accion = CasillasAdyacentes.OESTE;
            }
        } else if (lastAccion == CasillasAdyacentes.SUR) {
            if (!vecCaracteristicas[V_SUR]) {
                accion = CasillasAdyacentes.SUR;
            } else if (!vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.OESTE;
            } else {
                accion = CasillasAdyacentes.NORTE;
            }
        } else if (lastAccion == CasillasAdyacentes.OESTE) {
            if (!vecCaracteristicas[V_OESTE]) {
                accion = CasillasAdyacentes.OESTE;
            } else if (!vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else {
                accion = CasillasAdyacentes.ESTE;
            }
        } else if (lastAccion == CasillasAdyacentes.NORTE) {
            if (!vecCaracteristicas[V_NORTE]) {
                accion = CasillasAdyacentes.NORTE;
            } else if (!vecCaracteristicas[V_ESTE]) {
                accion = CasillasAdyacentes.ESTE;
            } else {
                accion = CasillasAdyacentes.SUR;
            }
        }
        return accion;
    }

    public void printPercepciones() {
        int i = 0;
        System.out.print("[");
        for (i = 0; i < percepciones.length - 1; i++) {
            System.out.print(percepciones[i] + ", ");
        }
        System.out.print(percepciones[i]);
        System.out.println("]");
    }

    public void printCaracteristicas(boolean[] vec) {
        int i = 0;
        System.out.print("[");
        for (i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ", ");
        }
        System.out.print(vec[i]);
        System.out.println("]");
    }

    private boolean contains(boolean[] b, boolean mark) {
        for (int i = 0; i < b.length; i++) {
            if (b[i] == mark) {
                return true;
            }
        }
        return false;
    }

    public void startStop() {
        auto = !auto;
        if (mutex.hasQueuedThreads()) {
            mutex.release();
        }
    }

    public void nextAccion() {
        mutex.release();
    }
}
