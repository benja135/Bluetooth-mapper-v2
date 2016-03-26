/**
 * TER Lego 2015 - Université Paul Sabatier
 * @author LACHERAY Benjamin, ANTOINE Kevin, MOUGEOT Matteo
 * 
 */

package bluetoothMapperView;

/**
 * Permet de dessiner l'interface ainsi que de gérer les événements associer aux boutons.
 * Code généré en parti avec NetBeans.
 * 
 */

@SuppressWarnings("serial")
public class Window extends javax.swing.JFrame {

    private NXTConnection nxt;

    /**
     * Creates new form NewJFrame
     */
    public Window(NXTConnection nxt) {
        this.nxt = nxt;
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        LabyrintheEtAffichage = new LabyrintheEtAffichage();
        jLabelText = new javax.swing.JLabel();
        phase = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFichier = new javax.swing.JMenu();
        jMenuItemConnexion = new javax.swing.JMenuItem();
        jMenuItemDeconnexion = new javax.swing.JMenuItem();
        jMenuItemGotoP2 = new javax.swing.JMenuItem();
        jMenuItemQuitter = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bluetooth Mapper View");
        setMinimumSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout jPanelLabyrinthe1Layout = new javax.swing.GroupLayout(LabyrintheEtAffichage);
        LabyrintheEtAffichage.setLayout(jPanelLabyrinthe1Layout);
        jPanelLabyrinthe1Layout.setHorizontalGroup(jPanelLabyrinthe1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanelLabyrinthe1Layout.setVerticalGroup(jPanelLabyrinthe1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 399, Short.MAX_VALUE));

        jLabelText.setText("Phase : ");

        phase.setText("1");

        jMenuFichier.setText("Fichier");

        jMenuItemConnexion.setText("Connexion");
        jMenuItemConnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConnexionActionPerformed(evt);
            }
        });
        jMenuFichier.add(jMenuItemConnexion);

        jMenuItemDeconnexion.setText("Déconnexion");
        jMenuItemDeconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeconnexionActionPerformed(evt);
            }
        });
        jMenuFichier.add(jMenuItemDeconnexion);

        jMenuItemGotoP2.setText("Reprendre à la phase 2");
        jMenuItemGotoP2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGotoP2ActionPerformed(evt);
            }
        });
        jMenuFichier.add(jMenuItemGotoP2);

        jMenuItemQuitter.setText("Quitter");
        jMenuItemQuitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitterActionPerformed(evt);
            }
        });
        jMenuFichier.add(jMenuItemQuitter);

        jMenuBar.add(jMenuFichier);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(LabyrintheEtAffichage, javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabelText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(phase)
                        .addContainerGap(739, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(LabyrintheEtAffichage, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelText).addComponent(phase))
                        .addGap(12, 12, 12)));

        pack();
    }// </editor-fold>

    private void jMenuItemConnexionActionPerformed(java.awt.event.ActionEvent evt) {
        nxt.connexion();
    }

    private void jMenuItemDeconnexionActionPerformed(java.awt.event.ActionEvent evt) {
        nxt.deconnexion();
    }

    private void jMenuItemQuitterActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        System.exit(0);
    }

    private void jMenuItemGotoP2ActionPerformed(java.awt.event.ActionEvent evt) {
        phase.setText("2");
    }

    private javax.swing.JLabel jLabelText;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFichier;
    private javax.swing.JMenuItem jMenuItemConnexion;
    private javax.swing.JMenuItem jMenuItemDeconnexion;
    private javax.swing.JMenuItem jMenuItemGotoP2;
    private javax.swing.JMenuItem jMenuItemQuitter;
    public LabyrintheEtAffichage LabyrintheEtAffichage;
    public javax.swing.JLabel phase;
}
