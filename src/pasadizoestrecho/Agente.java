package pasadizoestrecho;

import IU.CustomException;
import IU.Tablero;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor Manuel Blanes Castro
 */
public class Agente {

    private final int NORTE = 1;
    private final int NORESTE = 2;
    private final int ESTE = 3;
    private final int SURESTE = 4;
    private final int SUR = 5;
    private final int SUROESTE = 6;
    private final int OESTE = 7;
    private final int NOROESTE = 0;
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
    private ConjuntoAcciones lastAccion;
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
            ConjuntoAcciones accion = null;
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
            if (lastAccion == ConjuntoAcciones.ESTE && lastVecCaracteristicas[V_CARDINALES]) {
                // Por defecto intenta continuar en la misma direccion que la anterior, 
                // si no puede va comprobando si puede rotar en sentido antihorario
                if (vecCaracteristicas[V_NORTE]) {
                    if (vecCaracteristicas[V_ESTE]) {
                        if (vecCaracteristicas[V_SUR]) {
                            accion = ConjuntoAcciones.OESTE;
                        } else {
                            accion = ConjuntoAcciones.SUR;
                        }
                    } else {
                        accion = ConjuntoAcciones.ESTE;
                    }
                } else {
                    accion = ConjuntoAcciones.NORTE;
                }
            } else if (lastAccion == ConjuntoAcciones.SUR && lastVecCaracteristicas[V_CARDINALES]) {
                if (vecCaracteristicas[V_ESTE]) {
                    if (vecCaracteristicas[V_SUR]) {
                        if (vecCaracteristicas[V_OESTE]) {
                            accion = ConjuntoAcciones.NORTE;
                        } else {
                            accion = ConjuntoAcciones.OESTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.SUR;
                    }
                } else {
                    accion = ConjuntoAcciones.ESTE;
                }
            } else if (lastAccion == ConjuntoAcciones.OESTE && lastVecCaracteristicas[V_CARDINALES]) {
                if (vecCaracteristicas[V_SUR]) {
                    if (vecCaracteristicas[V_OESTE]) {
                        if (vecCaracteristicas[V_NORTE]) {
                            accion = ConjuntoAcciones.ESTE;
                        } else {
                            accion = ConjuntoAcciones.NORTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.OESTE;
                    }
                } else {
                    accion = ConjuntoAcciones.SUR;
                }
            } else if (lastAccion == ConjuntoAcciones.NORTE && lastVecCaracteristicas[V_CARDINALES]) {
                if (vecCaracteristicas[V_OESTE]) {
                    if (vecCaracteristicas[V_NORTE]) {
                        if (vecCaracteristicas[V_ESTE]) {
                            accion = ConjuntoAcciones.SUR;
                        } else {
                            accion = ConjuntoAcciones.ESTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.NORTE;
                    }
                } else {
                    accion = ConjuntoAcciones.OESTE;
                }
            } else if (vecCaracteristicas[V_CARDINALES] && lastVecCaracteristicas[V_INTERCARDINALES]
                    && !lastVecCaracteristicas[V_CARDINALES]) { //"Se encarga de la accion posterior a una rotacion"
                // Reevalua la direccion que tiene que seguir en el caso posterior a un cambio de direccion
                // Por defecto intenta continuar en la misma direccion que habia tomado
                // Si no puede, intenta girar en sentido antihorario
                if (lastAccion == ConjuntoAcciones.OESTE) {
                    if (vecCaracteristicas[V_OESTE]) {
                        if (vecCaracteristicas[V_NORTE]) {
                            accion = ConjuntoAcciones.ESTE;
                        } else {
                            accion = ConjuntoAcciones.NORTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.OESTE;
                    }
                } else if (lastAccion == ConjuntoAcciones.ESTE) {
                    if (vecCaracteristicas[V_ESTE]) {
                        if (vecCaracteristicas[V_SUR]) {
                            accion = ConjuntoAcciones.OESTE;
                        } else {
                            accion = ConjuntoAcciones.SUR;
                        }
                    } else {
                        accion = ConjuntoAcciones.ESTE;
                    }
                } else if (lastAccion == ConjuntoAcciones.NORTE) {
                    if (vecCaracteristicas[V_NORTE]) {
                        if (vecCaracteristicas[V_ESTE]) {
                            accion = ConjuntoAcciones.SUR;
                        } else {
                            accion = ConjuntoAcciones.ESTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.NORTE;
                    }
                } else if (lastAccion == ConjuntoAcciones.SUR) {
                    if (vecCaracteristicas[V_SUR]) {
                        if (vecCaracteristicas[V_OESTE]) {
                            accion = ConjuntoAcciones.NORTE;
                        } else {
                            accion = ConjuntoAcciones.OESTE;
                        }
                    } else {
                        accion = ConjuntoAcciones.SUR;
                    }
                }
            } else {
                // Accion por defecto, por defecto el agente ira norte, si no 
                // puede ir norte intentara girar en sentido horario
                if (vecCaracteristicas[V_CARDINALES]) {
                    if (vecCaracteristicas[V_NORTE]) {
                        if (vecCaracteristicas[V_ESTE]) {
                            accion = ConjuntoAcciones.SUR;
                        } else {
                            accion = ConjuntoAcciones.ESTE;
                        }
                    } else if (vecCaracteristicas[V_ESTE]) {
                        accion = ConjuntoAcciones.SUR;
                    } else if (vecCaracteristicas[V_OESTE]) {
                        accion = ConjuntoAcciones.NORTE;
                    }
                } else {
                    accion = ConjuntoAcciones.NORTE;
                }
            }
            tbl.moverPlayer(accion);
            lastVecCaracteristicas = vecCaracteristicas;
            lastAccion = accion;
            tbl.repaint();
        }
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
