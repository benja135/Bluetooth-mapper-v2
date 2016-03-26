/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Fourni des déplacement optimisé en prenant en compte le diamétre et
 * l'écartement des roues (respectivement 56 et 120 pour notre robot)
 * ainsi que des méthodes de redressements.
 * 
 * La vitesse des redressements et de la tête est fixée au maximum.
 * 
 * La vitesse pour utiliser forward()/backward() et les virages doit être 
 * définie avec setTravelSpeed() (en mm/s, ou l'unité donné à la construction)
 *
 */
public class DeplacementOp extends DifferentialPilot {

    public DeplacementOp() {
        super(WHEEL_DIAMETER, TRACK_WIDTH, MOTOR_LEFT, MOTOR_RIGHT);
    }

    /** Met la vitesse au maximum */
    public void setMaxSpeed() {
        setTravelSpeed(getMaxTravelSpeed());
    }

    /**
     * Virage en épingle : tourne sur lui même à 90 degrés vers le coté donné en paramétre.
     * 
     * @param cote
     */
    public void hairpinTurnTo(int cote) {
        if (cote == LEFT) {
            arc(0, 90);
        } else if (cote == RIGHT) {
            arc(0, -90);
        }
    }

    /**
     * Tourne en faisant un vrai arc de cercle. Le rayon de l'arc est égale à l'écartement des roues.
     * 
     * @param cote
     */
    public void arcTurnTo(int cote) {
        if (cote == LEFT) {
            arc(TRACK_WIDTH, 90);
        } else if (cote == RIGHT) {
            arc(-TRACK_WIDTH, -90);
        }
    }

    /** Demi tour */
    public void backTurn() {
        arc(0, -180);
    }

    /**
     * Eloigne le robot du mur sur son coté, la direction du robot n'est pas changée.
     * 
     * @param cote
     */
    public void redressement(int cote) {
        int angle = -200;

        MOTOR_LEFT.setSpeed(MOTOR_LEFT.getMaxSpeed());
        MOTOR_RIGHT.setSpeed(MOTOR_RIGHT.getMaxSpeed());

        if (cote == LEFT) {
            MOTOR_LEFT.rotate(angle);
            MOTOR_RIGHT.rotate(angle);
        } else if (cote == RIGHT) {
            MOTOR_RIGHT.rotate(angle);
            MOTOR_LEFT.rotate(angle);
        }
    }

    /**
     * Tourne la tête sur le coté donné en paramétre, avant le lancement du programme
     * la tête doit être positionné coté face !
     * 
     * @param cote
     */
    public void headTurnTo(int cote) {
        MOTOR_HEAD.setSpeed(MOTOR_HEAD.getMaxSpeed());
        switch (cote) {
        case LEFT:
            MOTOR_HEAD.rotateTo(90);
            break;
        case RIGHT:
            MOTOR_HEAD.rotateTo(-90);
            break;
        case FACE:
            MOTOR_HEAD.rotateTo(0);
            break;
        }
    }

    /**
     * On constate parfois un arrêt pas très précis avec la méthode travel déjà implémenté. 
     * Càd que les 2 roues ne s'arrêtent pas exactement en même temps et le robot prend 
     * un mauvais angle. myTravel vient corriger ce défaut.
     * La vitesse est celle définie par setTravelSpeep().
     * 
     * @param distance dans l'unité donnée (ici mm)
     * @param stop false si on veut pas arrêter les moteurs (pour enchainer sur un virage par ex)
     * @throws InterruptedException
     */
    public void myTravel(double distance, boolean stop) throws InterruptedException {
        this.forward();
        Thread.sleep((long) (distance / this.getTravelSpeed() * 1000.0));
        if (stop) {
            this.stop();
        }
    }

    /** Surcharge de myTravel, stop par défaut */
    public void myTravel(double distance) throws InterruptedException {
        myTravel(distance, true);
    }

    /**
     * Corrige l'angle du robot grâce à la bande noire.
     * Cette méthode doit être appeléejuste après avoir détecté la bande,
     * le robot doit être à l'arrêt et ne pas avoir dépassé la bande.
     */
    public void recoveryAngleWithLine() {
        int it = 0;

        while (it < 2) {
            if (LIGHT_LEFT.getNormalizedLightValue() > lightLimit) {
                MOTOR_LEFT.setSpeed(180); // deg/s
                MOTOR_LEFT.forward();
                while (LIGHT_LEFT.getNormalizedLightValue() > lightLimit) {
                    Thread.yield();
                }
                MOTOR_LEFT.stop();
                Sound.beep();
            } else if (LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
                MOTOR_RIGHT.setSpeed(180);
                MOTOR_RIGHT.forward();

                while (LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
                    Thread.yield();
                }
                MOTOR_RIGHT.stop();
                Sound.beep();
            }

            if (it == 0) {
                travel(-30);
                setTravelSpeed(100); // mm/s
                forward();
                // Tant que c'est une couleur claire
                while (LIGHT_LEFT.getNormalizedLightValue() > lightLimit
                        && LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
                    Thread.yield();
                }
                Sound.beep();
                stop();
            }
            it++;
        }
    }

    /**
     * Corrige l'angle du robot par rapport au mur sur l'un ses cotés (le repositionne parallèle au mur).
     * 
     * @param cote
     * @throws InterruptedException
     */
    /*
     * public void recoveryAngle(int cote) throws InterruptedException {
     * 
     * double x, y, zprime, angle; double z = 100; UltrasonicSensor sonic;
     * 
     * if (cote == LEFT) { sonic = new UltrasonicSensor(SensorPort.S1); } else if (cote == RIGHT) { sonic = new
     * UltrasonicSensor(SensorPort.S2); }
     * 
     * // On avance puis on mesure x setTravelSpeed(50); travel(z/2); x = sonic.getDistance()*10; // On mesure x (*10
     * pour mettre en mm) Thread.sleep(50); // Temps pour que le capteur fasse son job correctement
     * 
     * // On recule puis on mesure y travel(-z); y = sonic.getDistance()*10; Thread.sleep(50);
     * 
     * // Théoréme de thalès && trigonométrie (voir schéma) zprime = (-z*x)/(x-y); angle = Math.asin(y/(zprime+z)); //
     * Angle en radian
     * 
     * travel(z/2); // On se repositionne au point de départ
     * 
     * // On corrige l'angle if (cote == LEFT) { arc(0, -180*angle/Math.PI); } else if (cote == RIGHT) { arc(0,
     * 180*angle/Math.PI); } }
     */
}
