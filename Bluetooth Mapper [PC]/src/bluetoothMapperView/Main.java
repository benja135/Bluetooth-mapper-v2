/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

import static bluetoothMapperView.Constantes.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import types.Point;

/**
 * Contient la méthode main du projet Adresse du NXT à changer
 */
public class Main {

    private static NXTConnection nxt;
    private static Window w;
    private static int phase = 1;
    private static FileReader fileReader;
    private static FileWriter fileWriter;

    /**
     * Méthode principale du projet coté PC
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        /* Set the Nimbus look and feel.
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        nxt = new NXTConnection("001653161388");
        w = new Window(nxt);
        w.setVisible(true);
        boolean actuPos = true;

        while (true) {
            if (nxt.isConnected()) {
                phase = Integer.valueOf(w.phase.getText());
                if (phase == 1) {
                    System.out.println("Attente d'un signal");
                    byte result = nxt.receive();

                    if (check(result, NXT_SCAN_CELL)) // NXT_SCAN_CELL détecté
                    {
                        System.out.println("NXT_SCAN_CELL reçu");

                        w.LabyrintheEtAffichage.scanOfNextCellIs(result, actuPos);
                        actuPos = true; // /!\ Toujours à true sauf si on vient d'envoyer une séquence !

                        if (!check(result, M_DROITE) && w.LabyrintheEtAffichage.cellNotVisited(M_DROITE)) {
                            nxt.send(MOVE_RIGHT);
                            w.LabyrintheEtAffichage.changementDirection(Ref.DROITE);
                        } else if (!check(result, M_AVANT) && w.LabyrintheEtAffichage.cellNotVisited(M_AVANT)) {
                            nxt.send(MOVE_FORWARD);
                        } else if (!check(result, M_GAUCHE) && w.LabyrintheEtAffichage.cellNotVisited(M_GAUCHE)) {
                            nxt.send(MOVE_LEFT);
                            w.LabyrintheEtAffichage.changementDirection(Ref.GAUCHE);
                        } else {
                            System.out.println("Impasse détectée");
                            sendSequence();     // Impasse
                            actuPos = false;    // On a déjà actualisé la position à la fin de l'envoi de la séq
                        }

                    } else if (check(result, NXT_FIN_DU_GAME)) {
                        System.out.println("NXT_FIN_DU_GAME reçu");

                        w.LabyrintheEtAffichage.closeTheLastCell(actuPos);
                        actuPos = true;

                        System.out.println("Final Cell: " + w.LabyrintheEtAffichage.getFinalCell());
                        Point cellToVisite = w.LabyrintheEtAffichage.returnCellNotVisited();

                        if (cellToVisite == null) // Si aucune autre cellule à visiter
                        {
                            // Enregistrement de la sequence la plus courte
                            File seq = new File("seq.txt");
                            fileWriter = new FileWriter(seq, false);

                            w.LabyrintheEtAffichage.setPos(10, 11); // Replace le robot à sa position initiale
                            w.LabyrintheEtAffichage.setDir(Dir.NORTH);

                            Deque<Dir> path = new ArrayDeque<Dir>();
                            path = w.LabyrintheEtAffichage.pathFinding(w.LabyrintheEtAffichage.getCellNxt(),
                                    w.LabyrintheEtAffichage.getFinalCell());

                            while (!path.isEmpty()) {
                                fileWriter.write(
                                        String.valueOf(w.LabyrintheEtAffichage.destToInstruction(path.pollLast())));
                            }
                            fileWriter.flush();
                            fileWriter.close();
                            // Fin enregistrement

                            nxt.send(STOP);
                            nxt.deconnexion();
                            phase = 2;
                            w.phase.setText("2");
                        } else {
                            sendSequence();
                            actuPos = false;
                        }

                    }
                } else if (phase == 2) // Charge et envoi la séquence
                {
                    File seq = new File("seq.txt");
                    try {
                        fileReader = new FileReader(seq);
                    } catch (FileNotFoundException e) {
                        System.out.println("Le fichier contenant la séquence est manquant.");
                        System.out.println("Le programme va quitter...");
                        System.exit(1);
                    }
                    nxt.send(SEQUENCE);
                    while (fileReader.ready()) {
                        nxt.send((byte) Character.getNumericValue(fileReader.read()));
                    }
                    nxt.send(SEQUENCE);
                    phase = 3; // Terminé !
                    w.phase.setText("3");
                }

            } else {
                Thread.sleep(500); // On est pas connecté alors on attend pour pas boucler trop vite
            }

        }
    }

    private static void sendSequence() {

        w.LabyrintheEtAffichage.resolveDeductibleCells();

        Point cellToVisite = w.LabyrintheEtAffichage.returnCellNotVisited();

        // S'il n'y pas une cellule inconnue, alors on récupére la cellule finale
        if (cellToVisite == null) {
            cellToVisite = w.LabyrintheEtAffichage.getFinalCell();
        }

        // S'il n'y a pas de cellule finale également, c'est qu'il y a un problème
        if (cellToVisite == null) {
            System.out.println("D'après les données cartographique, le labyrinthe a été entiérement parcouru.");
            System.out.println("Pourtant aucune ligne d'arrivée n'a été détecté.");
            System.out.println("Le programme va quitter. Faites un labyrinthe correct la prochaine fois :D");
            System.exit(1);
        }

        System.out.println("CellToVisite: " + cellToVisite.toString());

        Deque<Dir> path = new ArrayDeque<Dir>();
        path = w.LabyrintheEtAffichage.pathFinding(w.LabyrintheEtAffichage.getCellNxt(), cellToVisite);
        nxt.send(SEQUENCE);
        System.out.println(
                "Chemin pour " + w.LabyrintheEtAffichage.getCellNxt() + " " + cellToVisite.toString() + ": " + path);
        while (!path.isEmpty()) {
            nxt.send((byte) w.LabyrintheEtAffichage.destToInstruction(path.pollLast()));
        }
        nxt.send(SEQUENCE);

        w.LabyrintheEtAffichage.setPos(cellToVisite.x, cellToVisite.y); // Actualise la postion du robot
    }

}
