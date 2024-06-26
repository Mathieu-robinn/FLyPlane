/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package graphe;


import java.lang.Math;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
    
import org.graphstream.graph.implementations.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Random;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;


import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.painter.*;

/**
 *C:/Users/Robi6/OneDrive/Bureau/aeroports.txt
 *C:/Users/Robi6/OneDrive/Bureau/DataTest/vol-test1.csv
 * @author Robi6
 */

/**
 * La classe principale qui gère les aéroports, les vols et la création de graphes.
 */
public class GraphController {
    
    
    private static int minColors = Integer.MAX_VALUE;
    private static Map<Node, Integer> bestColoring = new HashMap<>();
    private static ListeAeroport L = new ListeAeroport();
    private static ListeVols LV = new ListeVols();       
    private static ListeVols LL = new ListeVols();       
    
    public GraphController(){
        
    }
    
    /**
     * Récupère la liste des aéroports.
     * @return la liste des aéroports
     */
    public ListeAeroport getlisteaero(){
        return L;
    }
    
    /**
     * Définit la liste des aéroports.
     * @param liste la nouvelle liste des aéroports
     */
    public void setlisteaero(ListeAeroport liste){
        L = liste;
    }
    
    /**
     * Récupère la liste des vols.
     * @return la liste des vols
     */
    public ListeVols getlisteVols(){
        return LV;
    }
    
    /**
     * Définit la liste des vols.
     * @param liste la nouvelle liste des vols
     */
    public void setlistevols(ListeVols liste){
        LV = liste;
    }
    
    /**
     * Crée un graphe à partir d'une liste de vols.
     * @param liste la liste de vols
     * @return la liste de vols mise à jour avec les arêtes
     */
    public ListeVols creationgraphe(ListeVols liste, int temps){
        int taille = liste.taille();
        int gpt = 0;
        int cpt=0;
        for (int i = 0; i< taille; i++){
            for (int y=i+1; y < taille; y++){
                cpt++;
                if(intersection(LV.getVolindice(i),liste.getVolindice(y), temps)){
                    gpt++;
                    liste.getVolindice(i).addadjacent(liste.getVolindice(y));
                    liste.getVolindice(y).addadjacent(liste.getVolindice(i));
                    
                    liste.addaerrete();
                    
                }
            }
        }     
        return liste;
        
    }

    
    
    /**
     * Vérifie si deux vols se croisent ou sont proches en temps et en espace.
     * @param V1 le premier vol
     * @param V2 le deuxième vol
     * @return true si les vols se croisent ou sont proches, false sinon
     */
    public static boolean intersection(Vol V1, Vol V2, int temps){
        
        if (V1 == V2){
            
            return false;
        }
        if ( V1.estAdjacent(V2)){
            
            return false;
            
        }
        if ( V2.estAdjacent(V1)){
            
            return false;
        }
        
        Aeroport A1 = L.accesAeroport(V1.getcodedepart());
        Aeroport A2 = L.accesAeroport(V1.getcodearrive());
        Aeroport A3 = L.accesAeroport(V2.getcodedepart());
        Aeroport A4 = L.accesAeroport(V2.getcodearrive());
        
        //A1 = A3 regarder  heure de depart
        if (A1 == A3 && (Math.abs(V1.getminutesdepart() - V2.getminutesdepart()) < temps)){
            return true;
        }
        //A2= A4 regarder heure arrivée
        if (A2 == A4 && (Math.abs(V1.getminutes_arrive() - V2.getminutes_arrive()) < temps)){
            return true;
        }
        //A1 = A4 regarder heure de depart de 1 puis heure d'arrivée de 2
        if (A1 == A4 && (Math.abs(V1.getminutesdepart() - V2.getminutes_arrive()) < temps)){
            return true;
        }
        if (A2 == A3 && (Math.abs(V1.getminutes_arrive() - V2.getminutesdepart()) < temps)){
            return true;
        }
        
        if (A1 ==  A4 && A2 == A3){
            if (Math.abs(V1.getminutesdepart() - V2.getminutesdepart()) < temps){
                return true;
            }
            if (Math.abs(V1.getminutes_arrive() - V2.getminutes_arrive()) < temps){
                return true;
            }
        }
        
        
        
        
        
        double intersection_x = ((A1.getX() * A2.getY() - A1.getY() * A2.getX()) * (A3.getX() - A4.getX()) - (A1.getX() - A2.getX()) * (A3.getX() * A4.getY() - A3.getY() * A4.getX())) / ((A1.getX() - A2.getX()) * (A3.getY() - A4.getY()) - (A1.getY() - A2.getY()) * (A3.getX() - A4.getX()));
        double intersection_y = ((A1.getX() * A2.getY() - A1.getY() * A2.getX()) * (A3.getY() - A4.getY()) - (A1.getY() - A2.getY()) * (A3.getX() * A4.getY() - A3.getY() * A4.getX())) / ((A1.getX() - A2.getX()) * (A3.getY() - A4.getY()) - (A1.getY() - A2.getY()) * (A3.getX() - A4.getX()));
                
        double min_x =  Math.min(A1.getX(), A2.getX());
        double max_x = Math.max(A1.getX(), A2.getX());
        double min_y = Math.min(A1.getY(), A2.getY());
        double max_y = Math.max(A1.getY(), A2.getY());
        
        // Répéter le même processus pour le second segment
        double min_x2 =  (Math.min(A3.getX(), A4.getX()));
        double max_x2 =  (Math.max(A3.getX(), A4.getX()));
        double min_y2 =  (Math.min(A3.getY(), A4.getY()));
        double max_y2 =  (Math.max(A3.getY(), A4.getY()));
        
        if (!((min_x <= intersection_x && intersection_x <= max_x) &&
            (min_y <= intersection_y && intersection_y <= max_y) &&
            (min_x2 <= intersection_x && intersection_x <= max_x2) &&
            (min_y2 <= intersection_y && intersection_y <= max_y2))) {
            return false;
        }
        
        
        double longueurX1 = A1.getX() - A2.getX();
        
        if( longueurX1 < 0){
            longueurX1 = longueurX1 * -1;
        }
        double var1 = (A1.getX() - intersection_x);
        if( var1 < 0){
            var1 = var1 * -1;
        }
        double pourcentage1 = var1/longueurX1;
        double heure1 = V1.getminutesdepart() +( V1.getduree() * pourcentage1);
        
        
        double longueurX2 = A3.getX() - A4.getX();
        
        if( longueurX2 < 0){
            longueurX2 = longueurX2 * -1;
        }
        double var2 = (A3.getX() - intersection_x);
        if( var2 < 0){
            var2 = var2 * -1;
        }
        double pourcentage2 = var2/longueurX2;
        double heure2 = V2.getminutesdepart() + (V2.getduree() * pourcentage2);
        
        
        if (Math.abs(heure1 - heure2) < temps) {
            return true; // Collision détectée
        }
        
        return false;
           
    }
    
        
    /**
     * Crée un graphe à partir du fichier texte fourni.
     * @param file le fichier contenant les données du graphe
     * @return un objet ListeVols représentant le graphe
     */
    public ListeVols CreateGraphText(File file) throws DonneeEroneException,FichierTropVolumineux {
        
        // Vérifier si le fichier existe
        if (!file.exists()) {
            System.out.println("Le fichier n'existe pas.");
            throw new DonneeEroneException(file);
            
        }else{
            System.out.println("Le fichier existe .");
            
        }
        
        try {
            if (estFichierTxt(file) && contientSeulementChiffresEtEspaces(file)) {
                System.out.println("Le fichier ne contient que des caractères chiffrés.");
            } else {
                System.out.println("Le fichier contient des caractères non chiffrés ou n'est pas un fichier texte valide.");
                throw new DonneeEroneException(file);
                
            }
        } catch (IOException e) {
            System.out.println("Une erreur s'est produite lors de la lecture du fichier : " + e.getMessage());
            throw new DonneeEroneException(file);
        }
        int nbsommets = 0;
        // Déclarer un BufferedReader pour lire le fichier
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Lire chaque ligne du fichier tant qu'il y en a
            ListeVols LVol = new ListeVols();
            int cpt = 0;
            while ((line = reader.readLine()) != null) {
                // Afficher chaque ligne lue
                if (cpt ==0){
                    int kmax = Integer.valueOf(line);
                    LVol.setkmax(kmax);
                    LVol.sethavekmax(true);
                    
                    
                }else if (cpt == 1){
                    nbsommets = Integer.valueOf(line);
                    LVol.setnbarrete(nbsommets);
                    for (int i =1; i <= nbsommets;i++){
                        Vol Vol = new Vol(i);
                        LVol.ajMembre(Vol);
                    }
                    
                }else{
                    String[] tab = line.split(" ");
                    if(tab.length != 2){
                        throw new DonneeEroneException(file);
                    }
                    if(file.length() > (1000000000 * 10)){
                        throw new FichierTropVolumineux();
                    }
                    int x = Integer.valueOf(tab[0]);
                    if(0 > x || nbsommets < x){
                        throw new DonneeEroneException(file);
                    }
                    int y = Integer.valueOf(tab[1]);
                    if(0 > y || nbsommets < y){
                        throw new DonneeEroneException(file);
                    }
                    Vol Vol1 = LVol.getVolnumero(x);
                    Vol Vol2 = LVol.getVolnumero(y);
                    Vol1.addadjacent(Vol2);
                    Vol2.addadjacent(Vol1);
                    
                    
                }
                
                cpt ++;
            }
            
            return LVol;
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier : " + e.getMessage());
        }
        return null;
        
        
        
    }
    
    public static boolean estFichierTxt(File fichier) {
        // Vérifie si le fichier existe, s'il est un fichier et s'il se termine par .txt
        return fichier.exists() && fichier.isFile() && fichier.getName().toLowerCase().endsWith(".txt");
    }

    public static boolean contientSeulementChiffresEtEspaces(File fichier) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            int caractere;
            while ((caractere = br.read()) != -1) {
                if (!Character.isDigit(caractere) && !Character.isWhitespace(caractere)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Calcule le diamètre du graphe.
     * @param liste l'objet ListeVols représentant le graphe
     * @return le diamètre du graphe
     */
    public double getDiametre(ListeVols liste){
        Graph G = getGraphStream(liste);
        return org.graphstream.algorithm.Toolkit.diameter(G);
    }

    /**
     * Génère un graphe GraphStream à partir d'un objet ListeVols.
     * @param liste l'objet ListeVols contenant les données des vols
     * @return un objet Graph pour la visualisation
     */
    public Graph getGraphStream(ListeVols liste) {
        Graph graph = new MultiGraph("multi graphe");
        ArrayList<String> couleurs = new ArrayList<>();
        Random random = new Random();
        
        // Générer des couleurs aléatoires pour chaque couleur utilisée dans la coloration
        for (int i = 0; i < liste.maxcouleur(); i++) {
            String couleur = String.format("#%02x%02x%02x", random.nextInt(256), random.nextInt(256), random.nextInt(256));
            couleurs.add("fill-color: " + couleur + ";");
        }

        // Ajouter tous les nœuds au graphe
        for (Vol vol : liste.getArraylist()) {
            String nodeId = Integer.toString(vol.getid());
            if (vol.hasname()){
                graph.addNode(nodeId).addAttribute("ui.label", vol.getnom());
            }else{
                graph.addNode(nodeId).addAttribute("ui.label", vol.getid());
            }
            graph.getNode(nodeId).addAttribute("ui.style", couleurs.get(vol.getcouleur() - 1));
        }

        // Ajouter toutes les arêtes au graphe
        for (Vol vol : liste.getArraylist()) {
            int id = vol.getid();
            for (int y = 0; y < vol.getnbadjacents(); y++) {
                Vol adjacent = vol.getAdjacentindice(y);
                    int adjacentId = adjacent.getid();
                    if (!graph.getNode(Integer.toString(id)).hasEdgeBetween(Integer.toString(adjacentId))) {
                        graph.addEdge("edge_" + id + "_" + adjacentId, Integer.toString(id), Integer.toString(adjacentId));
                    }
            }
        }

        return graph;
    }

    
    /**
     * Lit les données des vols à partir d'un fichier et les stocke dans un objet ListeVols.
     * @param file le fichier contenant les données des vols
     * @throws graphe.DonneeEroneException
     */
    public void setvolaeroports(File file) throws DonneeEroneException,FichierTropVolumineux {
        LV = new ListeVols();
        
        // Vérifier si le fichier existe
        if (!file.exists()) {
            System.out.println("Le fichier n'existe pas.");
            return;
        }else{
            System.out.println("Le fichier existe .");
        }      
        
        if(file.length() > 1000000000 * 10){
            throw new FichierTropVolumineux();
        }
        // Déclarer un BufferedReader pour lire le fichier
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Lire chaque ligne du fichier tant qu'il y en a
            while ((line = reader.readLine()) != null) {
                // Afficher chaque ligne lue
                String[] tab = line.split(";");
                
                if (tab.length != 6 || tab[1].length()!= 3 ||tab[2].length() != 3 || tab[3].length() > 2 || Integer.valueOf(tab[3]) > 23 || Integer.valueOf(tab[4]) < 0 || Integer.valueOf(tab[4]) > 180 || Integer.valueOf(tab[4]) > 59 || Integer.valueOf(tab[4]) < 0) {
                    throw new DonneeEroneException(file);
                }
                
                Vol Vol = new Vol(tab[0],tab[1],tab[2],Integer.valueOf(tab[3]),Integer.valueOf(tab[4]),Integer.valueOf(tab[5]));
                LV.ajMembre(Vol);
            }           
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }   
    
    
    /**
     * Lit les données des aéroports à partir d'un fichier et les stocke dans la liste appropriée.
     * @param file le fichier contenant les données des aéroports
     */
    public void setAeroportlist(File file) throws DonneeEroneException,FichierTropVolumineux{
        
        // Vérifier si le fichier existe
        if (!file.exists()) {
            System.out.println("Le fichier n'existe pas.");
            return;
        }else{
            System.out.println("Le fichier existe .");
        }
        L = new ListeAeroport();
        // Déclarer un BufferedReader pour lire le fichier
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Lire chaque ligne du fichier tant qu'il y en a
            while ((line = reader.readLine()) != null) {
                // Afficher chaque ligne lue
                
                /*System.out.println(tab[2].getClass().getTypeName());*/
                String[] tab = line.split(";");
                // ex : EBU;Saint-Etienne;45;32;26;N;4;17;47;E
                if (tab.length != 10 || tab[0].length() != 3 || tab[5].length() != 1 || tab[9].length() != 1) {
                throw new DonneeEroneException(file);
                }
                if(file.length() > 1000000000 * 10){
                        throw new FichierTropVolumineux();
                }
                Aeroport Aero = new Aeroport(tab[0],tab[1],Integer.valueOf(tab[2]),Integer.valueOf(tab[3]),Integer.valueOf(tab[4]),tab[5],Integer.valueOf(tab[6]),Integer.valueOf(tab[7]),Integer.valueOf(tab[8]),tab[9]);
                L.ajAeroport(Aero);
            }
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }

    
    /**
     * Applique l'algorithme de coloration LRF (Largest First) de manière répétitive pour obtenir une coloration optimale.
     * La liste des sommets est mélangée plusieurs fois et l'algorithme LRF est appliqué à chaque fois pour trouver 
     * la coloration avec le moins de conflits et le nombre minimal de couleurs.
     *
     * @param list la liste des vols (instances de ListeVols) à colorier.
     * @return la liste des vols colorée de manière optimale.
     */
    public ListeVols FULL_LRF(ListeVols list) {
        int minconflits = Integer.MAX_VALUE;
        ArrayList<Integer> best = list.getcouleurs();
        int mincouleur = Integer.MAX_VALUE;
        
        //int nb = 40;
        int nb = 20;
        //int nb= 6;
        for (int y = 0; y < nb; y++) {
            
            Collections.shuffle(list.getArraylist());
            list.setcouleurdefault();
            list.LRF();

            if ((list.maxcouleur() < mincouleur) || (list.maxcouleur() == mincouleur && list.getnbconflit() < minconflits)) {
                mincouleur = list.maxcouleur();
                best = list.getcouleurs();

                if (mincouleur == list.getkmax()) {
                    minconflits = list.getnbconflit();
                }
                if (minconflits == 0) {
                    break;
                }
            }
        }

        list.adresscouleurs(best);
        System.out.println("fin nb conflits  " + list.getnbconflit());
        System.out.println(list.goodcoloration());

        return list;
    }
    
    /**
     * Applique l'algorithme de coloration DSATUR de manière répétitive pour obtenir une coloration optimale.
     * La liste des sommets est mélangée plusieurs fois et l'algorithme DSATUR est appliqué à chaque fois pour trouver 
     * la coloration avec le moins de conflits et le nombre minimal de couleurs.
     *
     * @param list la liste des vols (instances de ListeVols) à colorier.
     * @return la liste des vols colorée de manière optimale.
     */
    public ListeVols DSaturFull(ListeVols list) {
        int minconflits = Integer.MAX_VALUE;
        ArrayList<Integer> best = list.getcouleurs();
        int mincouleur = Integer.MAX_VALUE;
        int cpt = 0;
        //int nb = list.getArraylist().size() * 3;
        int nb = list.getArraylist().size();
        //int nb = list.getArraylist().size() / 2;
        for (int y = 0; y < nb; y++) {
            cpt++;
            Collections.shuffle(list.getArraylist());
            list.setcouleurdefault();
            list.DsaturMAX();

            if ((list.maxcouleur() < mincouleur) || (list.maxcouleur() == mincouleur && list.getnbconflit() < minconflits)) {
                mincouleur = list.maxcouleur();
                best = list.getcouleurs();

                if (mincouleur == list.getkmax()) {
                    minconflits = list.getnbconflit();
                }
                if (minconflits == 0) {
                    break;
                }
            }
        }

        System.out.println("nb de boucles" + cpt);
        System.out.println("nb arretes" + list.getnbarrte());
        list.adresscouleurs(best);
        System.out.println("fin couleur min " + list.maxcouleur());
        System.out.println("fin nb conflits  " + list.getnbconflit());
        System.out.println(list.goodcoloration());

        return list;
    }
    
      
    /**
     * Applique l'algorithme de Welsh-Powell de manière exhaustive pour trouver la coloration optimale.
     *
     * @param list La liste de vols à colorier.
     * @return La liste de vols avec la coloration optimale trouvée.
     */
    public ListeVols FullWelshPowell(ListeVols list){
        
        int minconflits = Integer.MAX_VALUE;
        ArrayList<Integer> best = list.getcouleurs();
        int mincouleur = Integer.MAX_VALUE;
        int cpt = 0;
        int nb;
        if(list.getArraylist().size() > 500){
            nb = 300 * 300;
        }else{
            nb = list.getArraylist().size() * list.getArraylist().size();
        }
        
        
        for (int y =0;y<nb ;y++){
            cpt++;
            list.setcouleurdefault();
            Collections.shuffle(list.getArraylist());
            list.MAXWelshPowell();
            if ((list.maxcouleur() < mincouleur) || (list.maxcouleur() == mincouleur && list.getnbconflit() < minconflits)){
                mincouleur = list.maxcouleur();
                best = list.getcouleurs();
                if (mincouleur == list.getkmax()){
                    minconflits = list.getnbconflit();
                }
                if(minconflits == 0){
                    break;
                }
            }
            
        }
        System.out.println("nb de boucles"+ cpt);
        System.out.println("nb arretes"+ list.getnbarrte());
        list.adresscouleurs(best);
        System.out.println("fin couleur min " +list.maxcouleur());
        System.out.println("fin nb conflits  " +list.getnbconflit());
        System.out.println(list.goodcoloration());
        return list;
        
    }
    
    /**
     * Applique l'algorithme de coloration glouton de manière exhaustive pour trouver la coloration optimale.
     *
     * @param list La liste de vols à colorier.
     * @return La liste de vols avec la coloration optimale trouvée.
     */
    public ListeVols FullGreedyColor(ListeVols list){
        
        int minconflits = Integer.MAX_VALUE;
        ArrayList<Integer> best = list.getcouleurs();
        int mincouleur = Integer.MAX_VALUE;
        int nb;
        if(list.getArraylist().size() > 500){
            nb = 300 * 300;
        }else{
            nb = list.getArraylist().size() * list.getArraylist().size();
        }
        for (int y =0;y<nb ;y++){
            list.setcouleurdefault();
            Collections.shuffle(list.getArraylist());
            list.GreedyColor();
            if ((list.maxcouleur() < mincouleur) || (list.maxcouleur() == mincouleur && list.getnbconflit() < minconflits)){
                mincouleur = list.maxcouleur();
                best = list.getcouleurs();
                if (mincouleur == list.getkmax()){
                    minconflits = list.getnbconflit();
                }
                if(minconflits == 0){
                    break;
                }
            }
            
        }
        list.adresscouleurs(best);
        System.out.println("fin couleur min " +list.maxcouleur());
        System.out.println("fin nb conflits  " +list.getnbconflit());
        System.out.println(list.goodcoloration());
        return list;
        
    }
    
    /**
     * Calcule les statistiques du graphe des vols.
     *
     * @return Un objet Statistiques contenant les différentes métriques calculées.
     */
    
    public Statistiques calculerStatistiques(ListeVols list) {
        double degreMoyen = list.getdegremoyen();
        int nbComposantes = list.getnbcomposante();
        int nbNoeuds = list.taille();
        int nbAretes = list.getnbarrte();
        double diametre = getDiametre(list); // Assurez-vous que cette méthode existe et fonctionne
        int nbConflits = list.getnbconflit();

        return new Statistiques(degreMoyen, nbComposantes, nbNoeuds, nbAretes, diametre, nbConflits);
    }
    
    
}
