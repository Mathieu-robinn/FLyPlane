package graphe;

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
    private static ListeAeroport listeAeroport;
    private static ListeVols listeVolCarte;
    private static ListeVols listeVolGraphe;
    private static boolean graphgood = false;
    private static Main main;
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

    public InterfaceIHMSAE() {
        allgood = false;
        main = new Main();
        colorList = new ArrayList<>();
        airportIcon = new ImageIcon(getClass().getResource("/graphe/aero.png"));
        logoIcon = new ImageIcon(getClass().getResource("/graphe/logo.png"));
        setIconImage(logoIcon.getImage());
        setTitle("FlightSAE 1.0.0");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        JMenuBar menuBar = new JMenuBar();

        // JMenu

        JMenu menu1 = new JMenu("About");
        menuBar.add(menu1);
        JMenu menu2 = new JMenu("Files");
        menuBar.add(menu2);
        JMenu menu3 = new JMenu("Actions");
        menuBar.add(menu3);
        JCheckBoxMenuItem darkMode = new JCheckBoxMenuItem("Dark Mode");
        menuBar.add(darkMode);
        setJMenuBar(menuBar);

        // JMenuItems Files

        JMenuItem loadAirport = new JMenuItem("Load Airports");
        menu2.add(loadAirport);
        JPopupMenu.Separator separator = new JPopupMenu.Separator();
        menu2.add(separator);
        JMenuItem loadFlight = new JMenuItem("Load Flight");
        menu2.add(loadFlight);
        JPopupMenu.Separator separator1 = new JPopupMenu.Separator();
        menu2.add(separator1);
        JMenuItem loadGraphes = new JMenuItem("Load Graphes");
        menu2.add(loadGraphes);

        // JMenuItems Action

        JCheckBoxMenuItem enableKmax = new JCheckBoxMenuItem("K-max");
        menu3.add(enableKmax);
        JCheckBoxMenuItem enableColoring = new JCheckBoxMenuItem("Coloration");
        menu3.add(enableColoring);

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

        /*
        JCheckBox conflitsCheckbox = new JCheckBox("conflits");
        styleCheckBox(conflitsCheckbox, bgColor);
        rc.gridx = 0;
        rc.gridy = 1;
        rightControlPanel.add(conflitsCheckbox, rc);
        */

        JLabel kmaxLabel = new JLabel("K-max : ");
        kmaxLabel.setForeground(Color.WHITE);
        rc.gridx = 0;
        rc.gridy = 2;
        rc.anchor = GridBagConstraints.EAST;
        rightControlPanel.add(kmaxLabel, rc);

        kmaxSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        rc.gridx = 1;
        rc.gridy = 2;
        rightControlPanel.add(kmaxSpinner, rc);

        kmaxCheckbox = new JCheckBox("k-max");
        styleCheckBox(kmaxCheckbox, bgColor);
        rc.gridx = 0;
        rc.gridy = 0;
        rightControlPanel.add(kmaxCheckbox, rc);

        colorationCheckbox = new JCheckBox("coloring");
        styleCheckBox(colorationCheckbox, bgColor);
        rc.gridx = 0;
        rc.gridy = 1;
        rightControlPanel.add(colorationCheckbox, rc);

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
/*
        JButton startButton = new JButton("Start");
        styleButton(startButton, bgColor);
        b.gridx = 1;
        b.gridy = 0;
        bottomPanel.add(startButton, b);
*/

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

        JButton graphstreambutton = new JButton("Show GraphStream");
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

        algorithmComboBox = new JComboBox<>(new String[]{"Welsh et Powell", "Glouton"});
        b.gridx = 1;
        b.gridy = 1;
        bottomPanel.add(algorithmComboBox, b);
      /*
        JLabel kmaxLabel = new JLabel("K-max : ");
        kmaxLabel.setForeground(Color.WHITE);
        b.gridx = 2;
        b.gridy = 1;
        b.anchor = GridBagConstraints.EAST;
        bottomPanel.add(kmaxLabel, b);

        JSpinner kmaxSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
        b.gridx = 3;
        b.gridy = 1;
        bottomPanel.add(kmaxSpinner, b);

        */


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


        graphstreambutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood){
                    Graph G2 = main.getGraphStream(listeVolCarte);
                    GraphStreamFrame framegraphstream = new GraphStreamFrame(G2,"Graph with flights and airports");
                    framegraphstream.setVisible(true);
                }
                if(graphgood){
                    Graph G1 = main.getGraphStream(listeVolGraphe);
                    GraphStreamFrame framegraphstream = new GraphStreamFrame(G1, "simple graph");
                    framegraphstream.setVisible(true);
                }
            }
        });



        drawLinescouleurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood){
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
                if(allgood){
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
                if(allgood){
                    coloration();
                }
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statisticDialog();
            }
        });

        kmaxSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) ((JSpinner) e.getSource()).getValue();
                if (allgood){

                    listeVolCarte.setkmax(value);

                    if (listeVolCarte.havekmax()){
                        coloration();
                    }
                }
                System.out.println("Valeur actuelle : " + value);
                if (graphgood){

                    listeVolGraphe.setkmax(value);

                    if (listeVolGraphe.havekmax()){
                        coloration();
                    }
                }

            }
        });


        kmaxCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allgood){
                    if (kmaxCheckbox.isSelected()) {
                        listeVolCarte.sethavekmax(true);

                    } else {
                        listeVolCarte.sethavekmax(false);
                        ;
                    }

                    coloration();


                }
                if(graphgood){
                    if (kmaxCheckbox.isSelected()) {
                        listeVolGraphe.sethavekmax(true);

                    } else {
                        listeVolGraphe.sethavekmax(false);
                        ;
                    }
                    coloration();
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

        //pour tester les graphetest
        /*
        String FilePath = "C:/Users/Robi6/OneDrive/Bureau/DataTest/graph-test2.txt";
        //C:/Users/Emric/OneDrive/Bureau/S2/SaeFinal/sae_mathieu_petit_pirrera/DataTest/vol-test8.csv
        File file = new File(FilePath);
        listeVolGraphe = main.CreateGraphText(file);
        //listeVolGraphe = main.FullGreedyColor(listeVolGraphe);
        //listeVolGraphe.setcouleurdefault();

        //listeVolGraphe = main.FullWelshPowell(listeVolGraphe);
        listeVolGraphe = main.FullWelshPowell(listeVolGraphe);

        Graph G2 = main.getGraphStream(listeVolGraphe);
        G2.display();
        */

    }

    /**
    * Ouvre un sélecteur de fichiers pour choisir un fichier d'aéroports.
    */
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            //loadAeroportFile(selectedFile);
            try{
                main.setAeroportlist(selectedFile);
            }
            catch(DonneeVolException e){
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Please enter a valid file", "Wrong airport data", JOptionPane.ERROR_MESSAGE);
            }
            listeAeroport = main.getlisteaero();
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
            try{
               main.setvolaeroports(selectedFile);
            }
            catch(DonneeVolException e){
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Please enter a valid file", "Wrong flights data", JOptionPane.ERROR_MESSAGE);

            }
            listeVolCarte = main.getlisteVols();
            listeVolCarte = main.creationgraphe(listeVolCarte);
            listeVolCarte = main.FullGreedyColor(listeVolCarte);
            listeVolCarte.setkmax((int) kmaxSpinner.getValue());
            if(!graphgood){
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
            //loadAeroportFile(selectedFile);
            try{
                listeVolGraphe = main.CreateGraphText(selectedFile);
            }
            catch(DonneeVolException e){
                System.err.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "Veuillez saisir un fichier valide", "Wrong airports data", JOptionPane.ERROR_MESSAGE);
            }
            listeVolGraphe = main.FullGreedyColor(listeVolGraphe);
            listeVolGraphe.setkmax((int) kmaxSpinner.getValue());
            if(!allgood){
                setcolorlist();
            }
            graphgood = true;



        }
    }


    /**
    * Ajoute des marqueurs pour chaque aéroport de la liste des aéroports.
    */
    private void addAirportMarkers() {
        if (listeAeroport == null || listeAeroport.taillelisteaero() == 0) {
            System.out.println("Aucun aéroport à afficher.");
            return;
        }

        waypoints.clear();
        codeaero.clear();
        geoCondition.clear();

        for (int i = 0; i < listeAeroport.taillelisteaero(); i++) {
            Aeroport aeroport = listeAeroport.getaeroport(i);
            GeoPosition position = new GeoPosition(aeroport.getlongitude(), aeroport.getlatitude());
            waypoints.add(new MyWaypoint(aeroport.getcode(),position));
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
        for (int i = 0; i < listeVolCarte.taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = listeVolCarte.getVolindice(i);
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
                if (colorationCheckbox.isSelected()){
                    routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                }else{
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
        int kmax = listeVolCarte.maxcouleur();
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

    public void statisticDialog(){
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
                    Statistiques stats = main.calculerStatistiques(listeVolCarte);
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
                    Statistiques stats = main.calculerStatistiques(listeVolGraphe);
                    StatisticsFrame statsFrame = new StatisticsFrame(stats, "Simple Graphe");
                    statsFrame.setVisible(true);
                }
            });
        }

        // Affichage du dialog avec le panel
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
        for (int i = 0; i < listeVolCarte.taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = listeVolCarte.getVolindice(i);
            if(vol.getcouleur() == couleur){
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

                    if (colorationCheckbox.isSelected()){
                        routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                    }else{
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
    * @param heure  L'heure des vols à dessiner.
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
        System.out.println("coloration avec horaire, couleurs max : "+ listeVolCarte.maxcouleur());
        compoundPainter = new CompoundPainter<>();
        int minutes = heure * 60 + minute;
        for (int i = 0; i < listeVolCarte.taille(); i++) {
            List<GeoPosition> positions = new ArrayList<>();
            Vol vol = listeVolCarte.getVolindice(i);
            if (vol.getminutesdepart() <= minutes && vol.getminutes_arrive() >= minutes){
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

                    if (colorationCheckbox.isSelected()){
                        routePainter = new RoutePainter(positions, colorList.get(vol.getcouleur() + 1));
                    }else{
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
    * @param button  Le bouton à styliser.
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
    * @param bgColor  La couleur de fond de la case à cocher.
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
    private static void setcolorlist(){
        for (int i = 0; i < 100; i++) {
            Color randomColor = getRandomColor();
            colorList.add(randomColor);
        }
    }

    /**
    * Dessine les points sur la carte.
    */
    public static void dessinerpoints(){

        WaypointPainter<MyWaypoint> wp = new WaypointRenderer();
        compoundPainter.addPainter(wp);
        wp.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(compoundPainter);
        if(!allgood){
            for (MyWaypoint d : waypoints) {
                JButton button = d.getButton();
                button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    Object[][] data;
                    if(!allgood){
                        data = new Object[0][0];
                    }else{
                        data = listeVolCarte.getlistvolsfromaero(d.getName());
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
    public static void coloration(){
        if (allgood) { // Remplacer true par la condition allgood

            if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                System.out.println("glouton");
                listeVolCarte = main.FullGreedyColor(listeVolCarte);
            } else {
                System.out.println("welsh");
                listeVolCarte = main.FullWelshPowell(listeVolCarte);
            }

            if(lastaction ==  1){
                drawLinesAllVolsWithColoration();
            }else if(lastaction == 2){
                drawLinesColorVolsWithColoration(lastcouleur);
            }else if (lastaction == 3){
                drawLinesHourVolsWithColoration(lastheure, lastminute);
            }
        }
        if (graphgood) { // Remplacer true par la condition allgood

            if (algorithmComboBox.getSelectedItem().equals("Glouton")) {
                System.out.println("glouton");
                listeVolGraphe = main.FullGreedyColor(listeVolGraphe);
            } else {
                System.out.println("welsh");
                listeVolGraphe = main.FullWelshPowell(listeVolGraphe);
            }
        }


    }
}