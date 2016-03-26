/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

/**
 * Constantes utilisées dans plusieurs classes du projet.
 *
 */
public final class Constantes {

    // Empêche l'instanciation de la classe
    private Constantes() {
    }

    // Signaux que le PC envoie au NXT
    public static final byte MOVE_FORWARD = 1;
    public static final byte MOVE_LEFT = 2;
    public static final byte MOVE_RIGHT = 3;
    public static final byte MOVE_BACKWARD = 4;
    public static final byte STOP = 8;
    public static final byte SEQUENCE = 9;

    // Signaux que le NXT envoie au PC
    public static final byte NXT_SCAN_CELL = 0b00001000;
    public static final byte NXT_FIN_DU_GAME = 0b00010000;

    // Représentation des murs scannés dans le signal
    public static final byte M_DROITE = 0b00000001;
    public static final byte M_AVANT = 0b00000010;
    public static final byte M_GAUCHE = 0b00000100;

    public enum Dir {
        SOUTH, NORTH, EAST, WEST
    }

    public enum Ref {
        AVANT, GAUCHE, DROITE, ARRIERE
    }

    /**
     * Retourne vrai si les bits à 1 de "bits" sont aussi à 1 dans "message"
     */
    public static boolean check(byte message, byte bits) {
        return (message & bits) == bits; // & : "et" logique
    }

}
