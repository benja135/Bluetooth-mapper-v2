/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

/**
 * Constantes utilisées dans plusieurs classes du projet. 
 * Les valeurs des 2 premiéres catégories peuvent être changer
 * selont vos besoins (caractéristiques et seuils).
 *
 */
public final class Constantes {

    // Empêche l'instanciation de la classe
    private Constantes() {
    }

    // Caractéristiques du robot
    public static final int WHEEL_DIAMETER = 56;                        // Diamétre des roues
    public static final int TRACK_WIDTH = 120;                          // Mesure d'un centre d'une roue jusqu'au centre de l'autre
    public static final NXTRegulatedMotor MOTOR_LEFT = Motor.B;         // Le moteur gauche est branché sur quel port ?
    public static final NXTRegulatedMotor MOTOR_RIGHT = Motor.A;        // Et le droit ?
    public static final NXTRegulatedMotor MOTOR_HEAD = Motor.C;         // Moteur de la tête
    public static final LightSensor LIGHT_LEFT = new LightSensor(SensorPort.S2, true);
    public static final LightSensor LIGHT_RIGHT = new LightSensor(SensorPort.S3, true);
   
    /*
     * On initialise les capteurs de distance au début de Scanner 
     * et FastAndFurious car les initialisations ne sont pas les même.
     */

    /*
     * Seuils de détection : ces seuils sont plus ou moins calibrés pour notre robot et notre environnement
     */
    public static final int distanceFace = 28;          // si > alors il n'y a rien en face
    public static final int distanceGauche = 28;        // si > alors il n'y a rien sur le coté
    public static final int distanceDroit = 33;         // si > alors il n'y a rien sur le coté
    public static final int distanceMinDroite = 16;     // si < alors on redresse
    public static final int distanceMinGauche = 8;      // si < alors on redresse
    public static final int lightLimit = 450;           // si < alors c'est une bande noire

    // Signaux que le NXT envoie au PC
    public static final byte NXT_SCAN_CELL = 0b00001000;
    public static final byte NXT_FIN_DU_GAME = 0b00010000;

    // Représentation des murs scannés dans le signal
    public static final byte M_DROITE = 0b00000001;
    public static final byte M_AVANT = 0b00000010;
    public static final byte M_GAUCHE = 0b00000100;

    // Signaux que le PC envoie au NXT
    public static final byte MOVE_FORWARD = 1;
    public static final byte MOVE_LEFT = 2;
    public static final byte MOVE_RIGHT = 3;
    public static final byte MOVE_BACKWARD = 4;
    public static final byte STOP = 8;
    public static final byte SEQUENCE = 9;

    // Arguments des fonctions de déplacements
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int FACE = 2;

}
