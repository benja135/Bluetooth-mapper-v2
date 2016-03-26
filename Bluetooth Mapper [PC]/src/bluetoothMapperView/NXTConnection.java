/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

/**
 * Permet de gérer plus facilement la connexion au NXT
 * 
 */
public class NXTConnection {

    private NXTConnector conn;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean connexion = false;
    private String monNxt;

    public NXTConnection(String monNxt) {
        this.monNxt = monNxt;
    }

    /**
     * Initialisation de la connexion à Hydra, ouverture des streams d'écriture et de lecture
     */
    public void connexion() {

        conn = new NXTConnector();

        if (!connexion) {
            conn.addLogListener(new NXTCommLogListener() {

                public void logEvent(String message) {
                    System.out.println("BTSend Log.listener: " + message);
                }

                public void logEvent(Throwable throwable) {
                    System.out.println("BTSend Log.listener - stack trace: ");
                    throwable.printStackTrace();
                }
            });

            // Connect to any NXT over Bluetooth : "btspp://"
            // Adresse de Hydra : 001653161388 || Robot 2 : 00165318EB71
            System.out.println("Tentative de connexion à " + monNxt);
            connexion = conn.connectTo("btspp://" + monNxt);

            if (!connexion) {
                System.err.println("Failed to connect to any NXT");
            } else {
                System.out.println("Connexion OK");
                dos = new DataOutputStream(conn.getOutputStream());
                dis = new DataInputStream(conn.getInputStream());
            }

        } else {
            System.out.println("Vous êtes déjà connecté au NXT");
        }
    }

    /**
     * Envoie un octet au NXT
     * 
     * @param i
     */
    public void send(byte i) {
        if (connexion) {
            try {
                System.out.println("Sending : " + i);
                dos.write(i);
                dos.flush();

            } catch (IOException ioe) {
                System.out.println("IO Exception writing bytes:");
                System.out.println(ioe.getMessage());
            }
        } else {
            System.out.println("Vous n'êtes pas connecté au NXT");
        }
    }

    /**
     * Lit un octet sur le stream de lecture S'il n'y a "rien sur le stream", 
     * l'exeception est gérée, et la méthode ne se termine pas avant qu'un octet ait été lu.
     * (Cette exception nous a bcp posé probléme). EOF apparait la plupart du temps si un nxt est déconnecté.
     * 
     * @return l'octet lu, ou -1 si on est pas connecté !
     * @throws InterruptedException
     * @throws IOException
     */
    public byte receive() throws InterruptedException, IOException {
        if (connexion) {
            byte val = -2;

            while (val == -2) {
                try {
                    val = dis.readByte();
                } catch (NullPointerException e) {
                    val = -2;
                    System.out.println("IO Exception");
                    System.out.println(e.getMessage());
                    System.out.println("Reception Fail");
                } catch (EOFException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Un nxt s'est certainement déconnecté !");
                    System.out.println("Le programme va quitter.");
                    System.exit(1);
                }
                Thread.sleep(100);
            }
            System.out.println("Reception OK");
            return val;

        } else {
            System.out.println("Vous n'êtes pas connecté au NXT");
            return -1;
        }
    }

    /**
     * Déconnexion du NXT
     */
    public void deconnexion() {
        if (connexion) {
            try {
                dis.close();
                dos.close();
                conn.close();
            } catch (IOException ioe) {
                System.out.println("IOException closing connection:");
                System.out.println(ioe.getMessage());
            }
            connexion = false;
            System.out.println("Déconnexion OK");
        } else {
            System.out.println("Vous n'êtes pas connecté au NXT");
        }
    }

    /**
     * Est-on connecté ou pas ?
     */
    public boolean isConnected() {
        return connexion;
    }

}
