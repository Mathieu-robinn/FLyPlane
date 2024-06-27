package graphe;

import graphe.Modele;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.painter.CompoundPainter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.graphstream.graph.Graph;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;

public class InterfaceIHMSAE extends JFrame {

    private static ListeVolsFrame LVF = null;
    private static int lastaction = 0;
    private static int lastcouleur = 0;
    private static int lastminute = 0;
    private static int lastheure = 0;
    private static JXMapViewer mapViewer;
    private static Set<MyWaypoint> waypoints;

    private static boolean graphgood = false;
    private static Modele modele;
    private static CompoundPainter<JXMapViewer> compoundPainter;
    private static List<Color> colorList;
    private static ArrayList<String> codeaero;
    private static ArrayList<GeoPosition> geoCondition;
    private static JCheckBox colorationCheckbox;
    private static JCheckBox kmaxCheckbox;
    private static boolean allgood;
    private static JTextField heureField;
    private static JTextField minuteField;
    private static JSpinner kmaxSpinner;
    private static JMenuBar menuBar;
    private static Icon airportIcon;
    private static JComboBox<String> algorithmComboBox;
    private static ImageIcon logoIcon;
    private static JSpinner dureecolision;

    public InterfaceIHMSAE() {
        constrFen();
    }
    
    public void constrFen(){
        allgood = false;
        modele = new Modele();
        colorList = new ArrayList<>();
        airportIcon = new ImageIcon(getClass().getResource("/graphe/aero.png"));
        logoIcon = new ImageIcon(getClass().getResource("/graphe/logo.png"));

        setIconImage(logoIcon.getImage());
        setTitle("FlightSAE 1.0.0");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        constrPan();
    }
        
    public void constrPan(){
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        JMenuBar menuBar = new JMenuBar();

        // JMenu
        JMenu menu1 = new JMenu("About");
        menuBar.add(menu1);
        JMenu menu2 = new JMenu("Files");
        menuBar.add(menu2);
        JCheckBoxMenuItem darkMode = new JCheckBoxMenuItem("Dark Mode");
        menuBar.add(darkMode);
        setJMenuBar(menuBar);

        JMenuItem aboutItem = new JMenuItem("Information");
        menu1.add(aboutItem);

        // JMenuItems Files
        JMenuItem loadAirport = new JMenuItem("Load Airports");
        menu2.add(loadAirport);
        JPopupMenu.Separator separator = new JPopupMenu.Separator();
        menu2.add(separator);
        JMenuItem loadFlight = new JMenuItem("Load Flight");
        menu2.add(loadFlight);
        JPopupMenu.Separator separator1 = new JPopupMenu.Separator();
        menu2.add(separator1);
        JMenuItem loadGraphes = new JMenuItem("Load Graphs");
        menu2.add(loadGraphes);

        // JMenuItems Light Mode
        Color bgColor = Color.decode("#283C4F");
        getContentPane().setBackground(bgColor);
        compoundPainter = new CompoundPainter<>();
        codeaero = new ArrayList<>();
        geoCondition = new ArrayList<>();

        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        darkMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (darkMode.isSelected()) {
                    TileFactoryInfo satelliteInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
                    DefaultTileFactory satelliteTileFactory = new DefaultTileFactory(satelliteInfo);
                    mapViewer.setTileFactory(satelliteTileFactory);
                } else {
                    // Set back to default (light) mode tile source
                    mapViewer.setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo()));
                    mapViewer.setZoom(13);
                }
            }
        });

        GeoPosition france = new GeoPosition(46.603354, 1.888334);
        mapViewer.setZoom(13);
        mapViewer.setAddressLocation(france);

        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(mapViewer, gbc);

        JPanel leftControlPanel = new JPanel();
        leftControlPanel.setLayout(new GridBagLayout());
        leftControlPanel.setBackground(bgColor);
        GridBagConstraints lc = new GridBagConstraints();
        lc.insets = new Insets(5, 5, 5, 5);
        lc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel(logoIcon);
        lc.gridx = 0;
        lc.gridy = 0;
        leftControlPanel.add(logoLabel, lc);

        lc.gridy = 1; // Commencer les autres composants en y=1
        JButton aeroportsButton = new JButton("Load airports");
        styleButton(aeroportsButton, bgColor);
        leftControlPanel.add(aeroportsButton, lc);

        // Continuez avec les autres composants...
        lc.gridy++;
        JButton volsButton = new JButton("Load flights");
        styleButton(volsButton, bgColor);
        leftControlPanel.add(volsButton, lc);

        lc.gridy++;
        JButton graphesButton = new JButton("Load graphs");
        styleButton(graphesButton, bgColor);
        leftControlPanel.add(graphesButton, lc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(leftControlPanel, gbc);

        JPanel rightControlPanel = new JPanel();
        rightControlPanel.setLayout(new GridBagLayout());
        rightControlPanel.setBackground(bgColor);
        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(5, 5, 5, 5);
        rc.fill = GridBagConstraints.HORIZONTAL;

        JLabel kmaxLabel = new JLabel("K-max : ");
        kmaxLabel.setForeground(Color.WHITE);
        rc.gridx = 0;
        rc.gridy = 2;
        rc.anchor = GridBagConstraints.EAST;
        rightControlPanel.add(kmaxLabel, rc);

        kmaxSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
        rc.gridx = 1;
        rc.gridy = 2;
        rightControlPanel.add(kmaxSpinner, rc);

        kmaxCheckbox = new JCheckBox("k-max");
        styleCheckBox(kmaxCheckbox, bgColor);
        rc.gridx = 0;
        rc.gridy = 0;
        rightControlPanel.add(kmaxCheckbox, rc);

        colorationCheckbox = new JCheckBox("Coloring");
        styleCheckBox(colorationCheckbox, bgColor);
        rc.gridx = 0;
        rc.gridy = 1;
        rightControlPanel.add(colorationCheckbox, rc);

        JLabel dureecolLabel = new JLabel("Safety Margin : ");
        dureecolLabel.setForeground(Color.WHITE);
        rc.gridx = 0;
        rc.gridy = 3;
        rc.anchor = GridBagConstraints.EAST;
        rightControlPanel.add(dureecolLabel, rc);

        dureecolision = new JSpinner(new SpinnerNumberModel(15, 1, 60, 1));
        rc.gridx = 1;
        rc.gridy = 3;
        rightControlPanel.add(dureecolision, rc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(rightControlPanel, gbc);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setBackground(bgColor);
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(5, 5, 5, 5);
        b.fill = GridBagConstraints.HORIZONTAL;

        JButton drawLinesButton = new JButton("Draw all Lines");
        styleButton(drawLinesButton, bgColor);
        b.gridx = 0;
        b.gridy = 0;
        bottomPanel.add(drawLinesButton, b);

        JButton drawLinesHourButton = new JButton("Draw Lines with hour");
        styleButton(drawLinesHourButton, bgColor);
        b.gridx = 1;
        b.gridy = 0;
        bottomPanel.add(drawLinesHourButton, b);

        JButton drawLinescouleurButton = new JButton("Draw Lines with color");
        styleButton(drawLinescouleurButton, bgColor);
        b.gridx = 2;
        b.gridy = 0;
        bottomPanel.add(drawLinescouleurButton, b);

        JButton graphstreambutton = new JButton("GraphStream");
        styleButton(graphstreambutton, bgColor);
        b.gridx = 2;
        b.gridy = 1;
        bottomPanel.add(graphstreambutton, b);

        JButton statsButton = new JButton("Statistics");
        b.gridx = 3;
        b.gridy = 0;
        styleButton(statsButton, bgColor);
        bottomPanel.add(statsButton, b);

        JLabel algorithmLabel = new JLabel("Selected Algorithm : ");
        algorithmLabel.setForeground(Color.WHITE);
        b.gridx = 0;
        b.gridy = 1;
        b.anchor = GridBagConstraints.EAST;
        bottomPanel.add(algorithmLabel, b);

        algorithmComboBox = new JComboBox<>(new String[]{"Welsh et Powell", "Glouton", "Dsatur", "LFR"});
        b.gridx = 1;
        b.gridy = 1;
        bottomPanel.add(algorithmComboBox, b);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);

        loadAirport.addActionListener(e -> {
            openFileChooser();
        });

        loadFlight.addActionListener(e -> {
            openFileChooserVols();
        });

        loadGraphes.addActionListener(e -> {
            openFileChooserGraph();
        });

        /**
         *  * Cette fenêtre affiche le nom du logiciel, sa version et les
         * développeurs, [informations du logiciel].
         */
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutDialog = new JDialog(InterfaceIHMSAE.this, "À propos", true);
                aboutDialog.setSize(300, 200);
                aboutDialog.setLocationRelativeTo(InterfaceIHMSAE.this);

                // Contenu de la JDialog
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                // Centrer les labels horizontalement
                JLabel nameLabel = new JLabel("FlightSAE");
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel versionLabel = new JLabel("Version: 1.0.0");
                versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel developersLabel = new JLabel("Credit: MATHIEU, PETIT, PIRRERA");
                developersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espace
                panel.add(nameLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                panel.add(versionLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                panel.add(developersLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));

                panel.add(nameLabel);
                panel.add(versionLabel);
                panel.add(developersLabel);

                aboutDialog.add(panel);
                aboutDialog.setVisible(true);
            }
        });

        graphstreambutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    Graph G2 = modele.getMain().getGraphStream(modele.getListeVolCarte());
                    GraphStreamFrame framegraphstream = new GraphStreamFrame(G2, "Graph with flights and airports");
                    framegraphstream.setVisible(true);
                }
                if (graphgood) {
                    Graph G1 = modele.getMain().getGraphStream(modele.getListeVolGraphe());
                    GraphStreamFrame framegraphstream = new GraphStreamFrame(G1, "simple graph");
                    framegraphstream.setVisible(true);
                }
            }
        });

        drawLinescouleurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    //regle a 1
                    openNumberDialog();
                }
            }
        });

        drawLinesHourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    openHourMinuteDialog();
                }
            }
        });

        drawLinesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    drawLinesAllVolsWithColoration();
                }

            }
        });

        aeroportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooser();
            }
        });

        volsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooserVols();
            }
        });

        graphesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooserGraph();
            }
        });

        colorationCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    coloration();
                }
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood || graphgood) {
                    statisticDialog();
                }
            }
        });

        kmaxSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) ((JSpinner) e.getSource()).getValue();
                if (allgood) {

                    modele.getListeVolCarte().setkmax(value);

                    if (modele.getListeVolCarte().havekmax()) {
                        coloration();
                    }
                }
                System.out.println("Valeur actuelle : " + value);
                if (graphgood) {

                    modele.getListeVolGraphe().setkmax(value);

                    if (modele.getListeVolGraphe().havekmax()) {
                        coloration();
                    }
                }

            }
        });

        kmaxCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) {
                    if (kmaxCheckbox.isSelected()) {
                        modele.getListeVolCarte().sethavekmax(true);

                    } else {
                        modele.getListeVolCarte().sethavekmax(false);

                    }

                    coloration();

                }
                if (graphgood) {
                    if (kmaxCheckbox.isSelected()) {
                        modele.getListeVolGraphe().sethavekmax(true);

                    } else {
                        modele.getListeVolGraphe().sethavekmax(false);

                    }
                    coloration();
                }
            }
        });
        
        dureecolision.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) ((JSpinner) e.getSource()).getValue();
                System.out.println(modele.getListeVolCarte().getnbarrte());
                if (allgood) {
                    
                    modele.setListeVolCarte(modele.getMain().creationgraphe(modele.getListeVolCarte(), (int) dureecolision.getValue()));
                    modele.setListeVolCarte(modele.getMain().FullGreedyColor(modele.getListeVolCarte()));
                    modele.getListeVolCarte().setkmax((int) kmaxSpinner.getValue());
                    coloration();

                }
                System.out.println("Valeur actuelle : " + value);
                System.out.println(modele.getListeVolCarte().getnbarrte());

            }
        });
        
        algorithmComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood) { 
                    modele.getListeVolCarte().setcouleurdefault();
                    if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                        System.out.println("glouton");
                        modele.setListeVolCarte(modele.getMain().FullGreedyColor(modele.getListeVolCarte()));
                    } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                        System.out.println("welsh");
                        modele.setListeVolCarte(modele.getMain().FullWelshPowell(modele.getListeVolCarte()));
                    } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                        System.out.println("Dsatur");
                        modele.setListeVolCarte(modele.getMain().DSaturFull(modele.getListeVolCarte()));
                    } else {
                        System.out.println("LFR");
                        modele.setListeVolCarte(modele.getMain().FULL_LRF(modele.getListeVolCarte()));
                    }
                    coloration();
                    
                }
                if (graphgood) { // Remplacer true par la condition allgood
                    modele.getListeVolGraphe().setcouleurdefault();
                    if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                        System.out.println("glouton");
                        modele.setListeVolGraphe(modele.getMain().FullGreedyColor(modele.getListeVolGraphe()));
                    } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                        System.out.println("welsh");
                        modele.setListeVolGraphe(modele.getMain().FullWelshPowell(modele.getListeVolGraphe()));
                    } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                        System.out.println("Dsatur");
                        modele.setListeVolGraphe(modele.getMain().DSaturFull(modele.getListeVolGraphe()));
                    } else {
                        System.out.println("LFR");
                        modele.setListeVolGraphe(modele.getMain().FULL_LRF(modele.getListeVolGraphe()));
                    }
                }
            }
        });
         
        JButton exitButton = new JButton("Exit");
        styleButton(exitButton, Color.decode("#007BFF"));
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 0, 20);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(exitButton, gbc);

        waypoints = new HashSet<>();        
    }
    
    /**
     * Ouvre un sélecteur de fichiers pour choisir un fichier d'aéroports.
     */
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                try {
                    modele.getMain().setAeroportlist(selectedFile);
                } catch (FichierTropVolumineux e) {
                    System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "The file is too big", "Please enter a valid file", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DonneeEroneException e) {
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Please enter a valid file", "Wrong airport data", JOptionPane.ERROR_MESSAGE);
            }
            modele.setListeAeroport(modele.getMain().getlisteaero());
            addAirportMarkers();

        }
    }

    /**
     * Ouvre un sélecteur de fichiers pour choisir un fichier de vols.
     */
    private void openFileChooserVols() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                try {
                    System.out.println(modele.getListeVolCarte());
                    modele.getMain().setvolaeroports(selectedFile);
                } catch (FichierTropVolumineux e) {
                    System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "File is too big", "Please enter a valid file", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DonneeEroneException e) {
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Please enter a valid file", "Wrong flights data", JOptionPane.ERROR_MESSAGE);

            }
            modele.setListeVolCarte(modele.getMain().getlisteVols());
            modele.setListeVolCarte(modele.getMain().creationgraphe(modele.getListeVolCarte(), (int) dureecolision.getValue()));
            modele.setListeVolCarte(modele.getMain().FullGreedyColor(modele.getListeVolCarte()));
            modele.getListeVolCarte().setkmax((int) kmaxSpinner.getValue());
            if (!graphgood) {
                setcolorlist();
            }
            allgood = true;

        }
    }

    /**
     * Ouvre un sélecteur de fichiers pour choisir un fichier de graphes.
     */
    private void openFileChooserGraph() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                try {
                    modele.setListeVolGraphe(modele.getMain().CreateGraphText(selectedFile));
                } catch (FichierTropVolumineux e) {
                    System.err.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "File is too big", "Please enter a valid file", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DonneeEroneException e) {
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Please enter a valid File", "Wrong graph data", JOptionPane.ERROR_MESSAGE);
                return;
            }
            modele.setListeVolGraphe(modele.getMain().FullGreedyColor(modele.getListeVolGraphe()));
            modele.getListeVolGraphe().setkmax((int) kmaxSpinner.getValue());
            if (!allgood) {
                setcolorlist();
            }
            graphgood = true;

        }
    }
    
    /**
     * Ajoute des marqueurs pour chaque aéroport de la liste des aéroports.
     */
    private void addAirportMarkers() {
        if (modele.getListeAeroport() == null || modele.getListeAeroport().taillelisteaero() == 0) {
            System.out.println("Aucun aéroport à afficher.");
            return;
        }
        
        waypoints.clear();
        codeaero.clear();
        geoCondition.clear();

        for (int i = 0; i < modele.getListeAeroport().taillelisteaero(); i++) {
            Aeroport aeroport = modele.getListeAeroport().getaeroport(i);
            GeoPosition position = new GeoPosition(aeroport.getlongitude(), aeroport.getlatitude());
            waypoints.add(new MyWaypoint(aeroport.getcode(), position));
            codeaero.add(aeroport.getcode());
            geoCondition.add(position);
        }

        dessinerpoints();
        System.out.println("Les aéroports sont maintenant affichés");
    }
    
    /**
     * Dessine toutes les lignes des vols avec coloration.
     */
    private static void drawLinesAllVolsWithColoration() {
        if (waypoints.isEmpty()) {
            System.out.println("Aucun waypoint disponible pour dessiner des lignes.");
            return;
        }
        compoundPainter = new CompoundPainter<>();
        lastaction = 1;
        RoutePainter routePainter;
        for (int i = 0; i < modele.getListeVolCarte().taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = modele.getListeVolCarte().getVolindice(i);
            String codedepart = vol.getcodedepart();
            String codearrivee = vol.getcodearrive();
            GeoPosition positionDepart = null;
            GeoPosition positionArrivee = null;

            for (int y = 0; y < codeaero.size(); y++) {
                if (codeaero.get(y).equals(codedepart)) {
                    positionDepart = geoCondition.get(y);
                } else if (codeaero.get(y).equals(codearrivee)) {
                    positionArrivee = geoCondition.get(y);
                }
            }

            if (positionDepart != null && positionArrivee != null) {
                positions.add(positionDepart);
                positions.add(positionArrivee);
                if (colorationCheckbox.isSelected()) {
                    routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                } else {
                    routePainter = new RoutePainter(positions, Color.BLUE);
                }

                compoundPainter.addPainter(routePainter);
            } else {
                System.out.println("Erreur : Impossible de trouver les positions pour le vol " + vol.toString());
            }
        }

        dessinerpoints();
        System.out.println("Les lignes entre les waypoints ont été dessinées avec coloration");
    }

    /**
     * Ouvre une boîte de dialogue pour saisir un numéro.
     */
    private void openNumberDialog() {
        int kmax = modele.getListeVolCarte().maxcouleur();
        JTextField numberField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Entrez un nombre entre 1 et " + kmax + ":"));
        myPanel.add(numberField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Veuillez saisir un numéro", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int number = Integer.parseInt(numberField.getText());
                if (number >= 1 && number <= kmax) {
                    drawLinesColorVolsWithColoration(number);
                } else {
                    JOptionPane.showMessageDialog(null, "Le nombre doit être compris entre 1 et " + kmax, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Veuillez saisir un numéro valide ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Ouvre une boîte de dialogue pour saisir une heure et une minute.
     */
    private void openHourMinuteDialog() {
        JPanel myPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heureLabel = new JLabel("Hour : ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        myPanel.add(heureLabel, gbc);

        JTextField heureField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        myPanel.add(heureField, gbc);

        JLabel minuteLabel = new JLabel("Minute : ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        myPanel.add(minuteLabel, gbc);

        JTextField minuteField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 1;
        myPanel.add(minuteField, gbc);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Veuillez saisir l'heure et les minutes", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int heure = Integer.parseInt(heureField.getText());
                int minute = Integer.parseInt(minuteField.getText());
                drawLinesHourVolsWithColoration(heure, minute);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "illez saisir des valeurs valides ​​pour les heures et les minutes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void statisticDialog() {
        JPanel myPanel1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int x = 0;

        if (allgood) {
            JButton btnFlight = new JButton("Statistique Flight");
            gbc.gridx = x++;
            gbc.gridy = 0;
            myPanel1.add(btnFlight, gbc);
            btnFlight.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.getRootFrame().dispose();
                    Statistiques stats = modele.getMain().calculerStatistiques(modele.getListeVolCarte());
                    StatisticsFrame statsFrame = new StatisticsFrame(stats, "flights and airports flight");
                    
                    statsFrame.setVisible(true);
                    
                }
            });
        }

        if (graphgood) {
            JButton btnFlight1 = new JButton("Statistique Graphe");
            gbc.gridx = x;
            gbc.gridy = 0;
            myPanel1.add(btnFlight1, gbc);
            btnFlight1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.getRootFrame().dispose();
                    Statistiques stats = modele.getMain().calculerStatistiques(modele.getListeVolGraphe());
                    StatisticsFrame statsFrame = new StatisticsFrame(stats, "Simple Graphe");
                    statsFrame.setVisible(true);
                    
                }
            });
        }

        JOptionPane.showMessageDialog(null, myPanel1, "Statistiques", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Dessine les lignes des vols ayant la couleur spécifiée.
     *
     * @param couleur La couleur des vols à dessiner.
     */
    private static void drawLinesColorVolsWithColoration(int couleur) {

        if (waypoints.isEmpty()) {
            System.out.println("Aucun waypoint n'est disponible pour tracer les lignes.");
            return;
        }
        lastaction = 2;
        lastcouleur = couleur;
        compoundPainter = new CompoundPainter<>();
        RoutePainter routePainter;
        for (int i = 0; i < modele.getListeVolCarte().taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = modele.getListeVolCarte().getVolindice(i);
            if (vol.getcouleur() == couleur) {
                String codedepart = vol.getcodedepart();
                String codearrivee = vol.getcodearrive();
                GeoPosition positionDepart = null;
                GeoPosition positionArrivee = null;

                for (int y = 0; y < codeaero.size(); y++) {
                    if (codeaero.get(y).equals(codedepart)) {
                        positionDepart = geoCondition.get(y);
                    } else if (codeaero.get(y).equals(codearrivee)) {
                        positionArrivee = geoCondition.get(y);
                    }
                }

                if (positionDepart != null && positionArrivee != null) {
                    positions.add(positionDepart);
                    positions.add(positionArrivee);

                    if (colorationCheckbox.isSelected()) {
                        routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                    } else {
                        routePainter = new RoutePainter(positions, Color.BLUE);
                    }
                    compoundPainter.addPainter(routePainter);
                } else {
                    System.out.println("Erreur : Impossible de trouver les positions de vol " + vol.toString());
                }
            }
        }

        dessinerpoints();
        System.out.println("Les lignes entre les points de passage ont été tracées.");
    }

    /**
     * Dessine les lignes des vols ayant lieu à l'heure spécifiée.
     *
     * @param heure L'heure des vols à dessiner.
     * @param minute Les minutes des vols à dessiner.
     */
    private static void drawLinesHourVolsWithColoration(int heure, int minute) {
        if (waypoints.isEmpty()) {
            System.out.println("Aucun waypoint n'est disponible pour tracer les lignes.");
            return;
        }
        lastaction = 3;
        lastminute = minute;
        lastheure = heure;
        RoutePainter routePainter;
        System.out.println("coloration avec horaire, couleurs max : " + modele.getListeVolCarte().maxcouleur());
        compoundPainter = new CompoundPainter<>();
        int minutes = heure * 60 + minute;
        for (int i = 0; i < modele.getListeVolCarte().taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = modele.getListeVolCarte().getVolindice(i);
            if (vol.getminutesdepart() <= minutes && vol.getminutes_arrive() >= minutes) {
                String codedepart = vol.getcodedepart();
                String codearrivee = vol.getcodearrive();
                GeoPosition positionDepart = null;
                GeoPosition positionArrivee = null;

                for (int y = 0; y < codeaero.size(); y++) {
                    if (codeaero.get(y).equals(codedepart)) {
                        positionDepart = geoCondition.get(y);
                    } else if (codeaero.get(y).equals(codearrivee)) {
                        positionArrivee = geoCondition.get(y);
                    }
                }

                if (positionDepart != null && positionArrivee != null) {
                    positions.add(positionDepart);
                    positions.add(positionArrivee);

                    if (colorationCheckbox.isSelected()) {
                        routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                    } else {
                        routePainter = new RoutePainter(positions, Color.BLUE);
                    }
                    compoundPainter.addPainter(routePainter);
                } else {
                    System.out.println("Erreur : Impossible de trouver les positions pour le vol " + vol.toString());
                }
            }
        }

        dessinerpoints();
        System.out.println("Les lignes entre les waypoints ont été dessinées avec coloration");
    }
    
    /**
     * Applique un style aux boutons.
     *
     * @param button Le bouton à styliser.
     * @param bgColor La couleur de fond du bouton.
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
    
    /**
     * Applique un style aux cases à cocher.
     *
     * @param checkBox La case à cocher à styliser.
     * @param bgColor La couleur de fond de la case à cocher.
     */
    private void styleCheckBox(JCheckBox checkBox, Color bgColor) {
        checkBox.setBackground(bgColor);
        checkBox.setForeground(Color.WHITE);
        checkBox.setOpaque(true);
    }

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfaceIHMSAE().setVisible(true);
            }
        });
    }

    /**
     * Génère une couleur aléatoire.
     *
     * @return Une couleur aléatoire.
     */
    private static Color getRandomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);

        return new Color(red, green, blue);
    }

    /**
     * Initialise la liste des couleurs avec des couleurs aléatoires.
     */
    private static void setcolorlist() {
        for (int i = 0; i < 100; i++) {
            Color randomColor = getRandomColor();
            colorList.add(randomColor);
        }
    }

    /**
     * Dessine les points sur la carte.
     */
    public static void dessinerpoints() {

        WaypointPainter<MyWaypoint> wp = new WaypointRenderer();
        compoundPainter.addPainter(wp);
        wp.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(compoundPainter);
        if (!allgood) {
            for (MyWaypoint d : waypoints) {
                JButton button = d.getButton();
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Object[][] data;
                        if (!allgood) {
                            data = new Object[0][0];
                        } else {
                            data = modele.getListeVolCarte().getlistvolsfromaero(d.getName());
                        }

                        LVF = null;
                        LVF = new ListeVolsFrame(data, d.getName());
                        LVF.setVisible(true);

                    }
                });
                mapViewer.add(d.getButton());
            }
        }

        mapViewer.repaint();
    }

    /**
     * Applique la coloration aux données.
     */
    
    public static void coloration() {
        if (allgood) { // Remplacer true par la condition allgood
            modele.getListeVolCarte().setcouleurdefault();
            if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                System.out.println("glouton");
                modele.setListeVolCarte(modele.getMain().FullGreedyColor(modele.getListeVolCarte()));
            } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                System.out.println("welsh");
                modele.setListeVolCarte(modele.getMain().FullWelshPowell(modele.getListeVolCarte()));
            } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                System.out.println("Dsatur");
                modele.setListeVolCarte(modele.getMain().DSaturFull(modele.getListeVolCarte()));
            } else {
                System.out.println("LFR");
                modele.setListeVolCarte(modele.getMain().FULL_LRF(modele.getListeVolCarte()));
            }
            
            if (lastaction == 1) {
                drawLinesAllVolsWithColoration();
            } else if (lastaction == 2) {
                drawLinesColorVolsWithColoration(lastcouleur);
            } else if (lastaction == 3) {
                drawLinesHourVolsWithColoration(lastheure, lastminute);
            }
        }
        if (graphgood) { // Remplacer true par la condition allgood
            modele.getListeVolGraphe().setcouleurdefault();
            if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                System.out.println("glouton");
                modele.setListeVolGraphe(modele.getMain().FullGreedyColor(modele.getListeVolGraphe()));
            } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                System.out.println("welsh");
                modele.setListeVolGraphe(modele.getMain().FullWelshPowell(modele.getListeVolGraphe()));
            } else if (algorithmComboBox.getSelectedItem().equals("Glouton")){
                System.out.println("Dsatur");
                modele.setListeVolGraphe(modele.getMain().DSaturFull(modele.getListeVolGraphe()));
            } else {
                System.out.println("LFR");
                modele.setListeVolGraphe(modele.getMain().FULL_LRF(modele.getListeVolGraphe()));
            }
        }

    }
}
