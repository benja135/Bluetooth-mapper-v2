/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Reçois une séquence de déplacement et l'effectue en utilisant des déplacements rapides.
 *
 */
public class FastAndFurious {

    private static ThreadStop emergencyStop = new ThreadStop();

    // Initialisation des capteurs
    private static UltrasonicSensor sonicRight = new UltrasonicSensor(SensorPort.S1);
    private static UltrasonicSensor sonicLeft = new UltrasonicSensor(SensorPort.S4);

    // Gestion des déplacements
    private static DeplacementOp deplacement = new DeplacementOp();

    // Pour la communication
    private static BTConnection btc;
    private static DataInputStream receive;

    private static int distanceMin = 6;

    public static void main(String[] args) throws IOException, InterruptedException {

        emergencyStop.setDaemon(true);
        emergencyStop.start();

        System.out.println("Wait for BT...");
        btc = Bluetooth.waitForConnection();
        receive = btc.openDataInputStream();
        System.out.println("Connected !");

        receiveAndDoSequence();

    }

    private static void receiveAndDoSequence() throws IOException, InterruptedException {

        System.out.println("Wait signal seq...");
        byte valeur = -2;
        while (valeur != SEQUENCE) {
            try {
                valeur = receive.readByte();
            } catch (NullPointerException e) {
                valeur = -2;
                System.out.println("Stream lecture vide");
            }
            System.out.println(valeur);
            Thread.sleep(100);
        }

        System.out.println("Wait for sequence..");
        List<Byte> sequence = new ArrayList<Byte>();
        valeur = receive.readByte();
        while (valeur != SEQUENCE) {
            sequence.add(valeur);
            valeur = receive.readByte();
        }
        System.out.println("Sequence reçue !");
        while (!sequence.isEmpty()) {
            switch (sequence.get(0)) {
            case MOVE_LEFT: // deplacement.stop();
                deplacement.hairpinTurnTo(LEFT);
                break;
            case MOVE_RIGHT: // deplacement.stop();
                deplacement.hairpinTurnTo(RIGHT);
                break;
            default:
                break;

            }
            sequence.remove(0);
            forwardRecoveryAndMoveToCenter();
        }
        deplacement.stop();
    }

    /**
     * Avance jusqu'à trouver une ligne noire, corrige l'angle du robot si besoin 
     * puis avance jusqu'au milieu de la cellule Ecarte le robot du mur s'il est trop proche.
     * 
     * @throws InterruptedException
     */
    private static void forwardRecoveryAndMoveToCenter() throws InterruptedException {

        int lightValueLeft;
        int lightValueRight;
        long decalage;
        boolean detect;

        decalage = 0;
        detect = false;

        deplacement.setMaxSpeed();
        deplacement.forward();

        while (!detect) { // Tant qu'on a pas détecté la ligne avec les 2 capteurs

            lightValueLeft = LIGHT_LEFT.getNormalizedLightValue();
            lightValueRight = LIGHT_RIGHT.getNormalizedLightValue();

            if (lightValueLeft < lightLimit && lightValueRight < lightLimit) {
                detect = true;
            } else if (lightValueLeft < lightLimit && lightValueRight > lightLimit) { // Que le gauche
                decalage = System.currentTimeMillis();

                while (LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
                    Thread.yield();
                }
                decalage = -(System.currentTimeMillis() - decalage); // Négatif pour différencié d'avec le droit
                detect = true;
            } else if (lightValueLeft > lightLimit && lightValueRight < lightLimit) { // Que le droite

                decalage = System.currentTimeMillis();
                while (LIGHT_LEFT.getNormalizedLightValue() > lightLimit) {
                    Thread.yield();
                }
                decalage = System.currentTimeMillis() - decalage;
                detect = true;
            }

        }

        Sound.beep();

        // entre -20ms et +20ms, on considére qu'il n'y pas de décallage
        if (decalage > 20) {
            deplacement.stop();
            Motor.B.forward();
            Thread.sleep(20 + decalage);
            Motor.A.forward();
        } else if (decalage < -20) {
            deplacement.stop();
            Motor.A.forward();
            Thread.sleep(20 - decalage);
            Motor.B.forward();
        }

        // Avance de 210mm et ne s'arrête pas ensuite !
        deplacement.myTravel(210, false);

        // Redressement si trop proche d'un mur
        if (sonicRight.getDistance() <= distanceMin) {
            deplacement.stop();
            deplacement.redressement(RIGHT);
        }
        if (sonicLeft.getDistance() <= distanceMin) {
            deplacement.stop();
            deplacement.redressement(LEFT);
        }
    }

}
