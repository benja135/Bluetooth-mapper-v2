/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package scanner;

import static scanner.Constantes.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Cartographie le labyrinthe
 *
 */
public class Scanner {

    // Arrêt lors del'appui sur un bouton
    private static ThreadStop emergencyStop = new ThreadStop();

    // Initialisation des capteurs
    private static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);

    // Gestion des déplacements
    private static DeplacementOp deplacement = new DeplacementOp();

    // Pour la communication
    private static BTConnection btc;
    private static DataInputStream receive;
    private static DataOutputStream send;

    // Cartographie terminée / ligne d'arrivée trouvé ?
    private static boolean termine = false;

    public static void main(String[] args) throws Exception {

        emergencyStop.setDaemon(true);
        emergencyStop.start();

        System.out.println("Wait for BT...");
        btc = Bluetooth.waitForConnection();
        receive = btc.openDataInputStream();
        send = btc.openDataOutputStream();
        System.out.println("Connected !");

        byte resultScan;

        while (!termine) {
            forwardRecoveryAndMoveToCenter();

            /*
             * Si on a pas trouvé la ligne d'arrivée c'est qu'on est au milieu d'une case inconnue, 
             * on va donc la scanner et envoyer les résulats au PC.
             */
            if (!termine) {
                resultScan = NXT_SCAN_CELL; // Signal de scan

                deplacement.headTurnTo(LEFT); // Normalement la tête est déjà à gauche
                if (sonic.getDistance() < distanceGauche) {
                    resultScan |= M_GAUCHE; // | : "ou" logique : ajoute le mur gauche dans le signal
                }

                deplacement.headTurnTo(FACE);
                if (sonic.getDistance() < distanceFace) {
                    resultScan |= M_AVANT;
                }

                deplacement.headTurnTo(RIGHT);
                if (sonic.getDistance() < distanceDroit) {
                    resultScan |= M_DROITE;
                }

                send.write(resultScan); // On envoie les résultats du scan
                send.flush();

                System.out.println("Wait indication.."); // Puis on attend l'indication venant du pc

                switch (receive.readByte()) {
                case MOVE_LEFT:
                    deplacement.hairpinTurnTo(LEFT);
                    break;
                case MOVE_RIGHT:
                    deplacement.hairpinTurnTo(RIGHT);
                    break;
                case MOVE_BACKWARD:
                    deplacement.backTurn();
                    break;
                case SEQUENCE:
                    receiveAndDoSequence();
                    break;

                default:
                    System.out.println("Réponse inattendue");
                    break;
                }
            } else // termine == true
            {
                send.write(NXT_FIN_DU_GAME); // Envoie du sinal de "ligne d'arrivée trouvé"
                send.flush();

                switch (receive.readByte()) {
                case STOP:
                    System.out.println("Fin scanner"); // Arrêt total
                    break;
                case SEQUENCE:
                    receiveAndDoSequence(); // Récup séquence case manquante
                    termine = false;
                    break;
                default:
                    System.out.println("Réponse inattendue"); // Arrêt total également
                    break;
                }
            }

        } // while

    }

    /**
     * Avance jusqu'à trouver une ligne noire, corrige l'angle du robot si 
     * besoin puis avance jusqu'au milieu de la cellule. Si une autre bande noire
     * est trouvé, alors termine passe à true. Ecarte le robot du mur s'il est trop proche.
     * 
     * @throws InterruptedException
     */
    private static void forwardRecoveryAndMoveToCenter() throws InterruptedException {

        deplacement.setTravelSpeed(120); // mm/s
        deplacement.forward();

        // Tant qu'on a pas vu de bande noire
        while (LIGHT_LEFT.getNormalizedLightValue() > lightLimit
                && LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
            Thread.yield();
        }
        deplacement.stop();
        Sound.beep();

        // Si un des capteurs n'est pas sur le scotch
        if (LIGHT_LEFT.getNormalizedLightValue() > lightLimit || LIGHT_RIGHT.getNormalizedLightValue() > lightLimit) {
            deplacement.recoveryAngleWithLine();
        }

        deplacement.setTravelSpeed(120);
        deplacement.forward();

        // Si on est là c'est qu'on est sur le scotch, tant qu'on est sur le scotch
        while (LIGHT_LEFT.getNormalizedLightValue() < lightLimit
                || LIGHT_RIGHT.getNormalizedLightValue() < lightLimit) {
            Thread.yield();
        }

        deplacement.setTravelSpeed(200);
        deplacement.travel(225, true);
        while (deplacement.isMoving()) {
            // Si on trouve une bande noir ici, c'est que c'est l'arrivé. || ou && ? that is the question !
            if (LIGHT_LEFT.getNormalizedLightValue() < lightLimit
                    && LIGHT_RIGHT.getNormalizedLightValue() < lightLimit) {
                Sound.buzz();
                termine = true;
            }
        }

        // Redressement si trop proche d'un mur
        deplacement.headTurnTo(RIGHT); // Normalement on est déjà à droite (sauf la 1ére fois)
        if (sonic.getDistance() <= distanceMinDroite) {
            deplacement.redressement(RIGHT);
        }
        deplacement.headTurnTo(LEFT);
        if (sonic.getDistance() <= distanceMinGauche) {
            deplacement.redressement(LEFT);
        }
    }

    /**
     * Recois une séquence et la place dans une liste. Puis effectue les actions
     * jusqu'à que la liste soit vide.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private static void receiveAndDoSequence() throws IOException, InterruptedException {

        System.out.println("Wait for sequence..");
        List<Byte> sequence = new ArrayList<Byte>();
        byte valeur = receive.readByte();
        while (valeur != SEQUENCE) {
            sequence.add(valeur);
            valeur = receive.readByte();
        }

        while (!sequence.isEmpty()) {
            switch (sequence.get(0)) {
            case MOVE_LEFT:
                deplacement.hairpinTurnTo(LEFT);
                break;
            case MOVE_RIGHT:
                deplacement.hairpinTurnTo(RIGHT);
                break;
            case MOVE_BACKWARD:
                deplacement.backTurn();
                break;
            default:
                break;
            }
            sequence.remove(0);
            if (!sequence.isEmpty()) {
                forwardRecoveryAndMoveToCenter();
            }
        }
        sequence.clear();
    }

}
