package vue;

import controleur.Controller;
import modele.*;
import modele.Action;
import obsv.Observer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
//import java.io.*;
//import sun.audio.*;
import java.io.IOException;

public class Vue {
    private JFrame cadre;
    private Ile ile;
    private Commandes commandes;

    public Vue(Modele modele) {
        cadre = new JFrame();
        cadre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cadre.setLocation(500, 500);
        cadre.setTitle("L'Ile interdite !");
        /*
        public void music(){
            AudioPlayer MGP=AudioPlayer.player;
            AudioStream BGM;
            AudioData MD;
            ContinuousAudioDataStream loop = null;
            try{
            BGM = new AudioStream(new FileInputStream("music.mp3"));
            MD = BGM.getData();
            loop = new ContinuousAudioDataStream(MD);
            }catch(IOException error)(){
                MGP.start(loop);
            }
        }*/
        cadre.setLayout(new FlowLayout());

        ile = new Ile(modele);
        commandes = new Commandes(modele);

        cadre.add(commandes);
        cadre.add(ile);

        cadre.pack();
        cadre.setVisible(true);
        //music();
    }
}

class Ile extends JPanel implements Observer, MouseListener {
    private Modele modele;
    private final static int TAILLE=100;
    private MouseEvent survole;
    private BufferedImage[] imageJoueurs;
    private BufferedImage[] imageArtefacts;
    private BufferedImage[] imageCoule;
    private BufferedImage[] imageTuilesInonde;
    private BufferedImage[] imageTuiles;
    private final Color[] couleurArtefact = {
            new Color(255, 0, 0, 170),
            new Color(0, 255, 0, 170),
            new Color(0, 0, 255, 170),
            new Color(255, 220, 0, 170)
    };

    private final static Color green = new Color(0, 225, 0);
    private final static Color blue = new Color(50, 25, 255);
    private final static Color black = new Color(0, 0, 0);

    public Ile(Modele modele){
        this.modele = modele;
        addMouseListener(this);
        modele.addObserver(this);

        Dimension d = new Dimension(TAILLE*Modele.LARGEUR, TAILLE*Modele.HAUTEUR);
        this.setPreferredSize(d);

        String[] cheminImages = {"img/rouge.png", "img/vert.png", "img/bleu.png", "img/jaune.png", "img/helico.png" };
        String[] filePathsArtefact = {"img/feu.png", "img/eau.png", "img/terre.png", "img/air.png"};
        imageJoueurs = new BufferedImage[5];
        imageTuiles= new BufferedImage[24];
        imageCoule= new BufferedImage[1];
        imageTuilesInonde=new BufferedImage[24];
        imageArtefacts = new BufferedImage[4];
        
        try {
                imageCoule[0] = ImageIO.read(new File("img/Coule.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        for(int i = 0; i < 5; i++){
            try {
                imageJoueurs[i] = ImageIO.read(new File(cheminImages[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<24;i++){
            try {
                //System.out.print("img/"+modele.nomTuiles.get(i)+"png");
                imageTuiles[i] = ImageIO.read(new File("img/"+modele.nomTuiles.get(i)+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        for(int i=0;i<24;i++){
            try {
                imageTuilesInonde[i] = ImageIO.read(new File("img/"+modele.nomTuiles.get(i)+"_Inonde.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < 4; i++){
            try{
                imageArtefacts[i] = ImageIO.read(new File("img/"+modele.nomTresors.get(i)+".png"));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.repaint();
        int nbzones=0;
        int tr=0;
        for(int i=0; i<Modele.LARGEUR; i++) {
            for(int j=0; j<Modele.HAUTEUR; j++) {
                if((i==0 && j==0)|| (i==0 && j==5) || (i==5 && j==0) || (i==5 && j==5)){
                    paintArtefact(g,modele.getZone(i,j),(i)*TAILLE,(j)*TAILLE,tr);
                    tr++;
                }
                else if ((i==0 && j==1) || (i==0 && j==4) || (i==1 && j==0) || (i==1 && j==5) || (i==4 && j==0) || (i==4 && j==5) || (i==5 && j==1) || (i==5 && j==4)) {
                    PaintTuile(g,modele.getZone(i,j),(i)*TAILLE,(j)*TAILLE,nbzones);
                }
                else{
                    PaintTuile(g,modele.getZone(i,j),(i)*TAILLE,(j)*TAILLE,nbzones);
                    
                    nbzones++;
                }
                
            }
        }
    }
    private void PaintTuile(Graphics g , Zone zone,int x,int y,int nb){
        Color c = Color.GRAY;

        if (zone.getEtat() == Etat.Normal) {
            c = Color.WHITE;
        } else if (zone.getEtat() == Etat.Submergee) {
            c = black;
        } else if (zone.getEtat() == Etat.Inondee) {
            c = blue;
        }

        if (mouseSelect(x, y)) g.setColor(c.darker()); else g.setColor(c);
        g.fillRect(x, y, TAILLE, TAILLE);
        g.setColor(c.darker());
        g.drawRect(x, y, TAILLE, TAILLE);
        g.setColor(c);
        g.fillRect(x, y, TAILLE, TAILLE);
        if(zone.getEtat()==Etat.Inondee){
            g.drawImage(imageTuilesInonde[nb],x,y,TAILLE,TAILLE,null);
        }
        else if(zone.getEtat()==Etat.Submergee){
            g.drawImage(imageCoule[0],x,y,TAILLE,TAILLE,null);
        }
        else{
            g.drawImage(imageTuiles[nb], x, y , TAILLE, TAILLE, null);
        }
        if (zone.hasJoueurOn()){
            Joueur[] on = zone.getJoueur();
            int i = 0;
            int j = 0;
            int t = TAILLE;
            if (on.length > 1) t = t/2;
            for (Joueur p : on) {
                if(i < TAILLE) {
                    g.drawImage(imageJoueurs[p.getNumJoueur()], x + i, y, t, t, null);
                    i+= TAILLE/2;
                }else{
                    g.drawImage(imageJoueurs[p.getNumJoueur()], x + j, y + TAILLE/2, t, t, null);
                    j+= TAILLE/2;
                }
            }
        }
    }
    
    private void paintArtefact(Graphics g,Zone zone,int x,int y,int t){
        g.drawImage(imageArtefacts[t], x, y , TAILLE, TAILLE, null);
    }

    


    private boolean mouseSelect(int x, int y){
        if (survole == null ) return false;
        boolean X = (x < survole.getX()) && (survole.getX() < (x + TAILLE));
        boolean Y = ((y < survole.getY()) && (survole.getY() < y + TAILLE));
        return (X && Y);
    }


    public void update() {
        repaint();
    }


    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        Zone z = modele.getZone((e.getX()/TAILLE), (e.getY()/TAILLE) );
        modele.actionJoueur(z);
        survole = e;
        super.repaint();
    }
    public void mouseReleased(MouseEvent e) {
        survole = null;
        super.repaint();
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}

class Commandes extends JPanel implements Observer {
    private Modele modele;
    private JLabel joueurLabel = new JLabel("Tour du joueur 1" );
    private JLabel artefactLabel = new JLabel("artefacts : ");
    private JLabel actionLabel = new JLabel("Actions dispos : 3/3");
    private JLabel objetLabel = new JLabel("Clefs : ");
    JLabel choixJoueur;
    boolean echange=false;
    int joueurechange;
    JButton validateButton,boutonArtefact,boutonRecherche,boutonDeplacement,boutonAsseche,boutonAvance,utiliseButton,echangeButton;
    JTextField caseText;
    JLabel[] boutonCarte=new JLabel[0];
    private JLabel boutonCarte1,boutonCarte2,boutonCarte0,boutonCarte3,boutonCarte4,boutonCarte5;
    int[] k;
    private ButtonGroup boutonsActions = new ButtonGroup();
    Controller act0,act1,act2,act3,act4,act5;
    private String[] labels=new String[6];
    Color[] colorsJoueur = {
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255),
            new Color(255, 200, 0)
    };


    public Commandes(Modele modele) {
        this.modele = modele;
        this.setLayout(new GridLayout(0, 1));
        modele.addObserver(this);

        boutonArtefact = new JButton("Prendre Artefact"); 
        this.add(boutonArtefact); 
        Controller take = new Controller(modele, Action.Artefact); 
        boutonArtefact.addActionListener(take); 

        boutonAvance = new JButton("Tour Suivant"); 
        this.add(boutonAvance); 
        Controller ctrl = new Controller(modele, Action.ProchainTour); 
        boutonAvance.addActionListener(ctrl); 

        boutonDeplacement = new JButton("Se \n déplacer");
        this.add(boutonDeplacement);
        Controller move = new Controller(modele, Action.Deplacement);
        boutonDeplacement.addActionListener(move);

        boutonAsseche = new JButton("Assécher\n");
        this.add(boutonAsseche);
        Controller dry = new Controller(modele, Action.Seche);
        boutonAsseche.addActionListener(dry);


        this.add(joueurLabel);
        this.add(actionLabel);
        this.add(artefactLabel);
        this.add(objetLabel);
        for(int i=0;i<modele.getCurrentPlayer().objet.size();i++){
            labels[i]=modele.getCurrentPlayer().objet.get(i).getCardName();
        }
        for(int i=modele.getCurrentPlayer().objet.size();i<6;i++){
            labels[i]="vide";
        }
        boutonCarte = new JLabel[modele.getCurrentPlayer().objet.size()];
        for(int i=0;i<modele.getCurrentPlayer().objet.size();i++){
            boutonCarte[i]=new JLabel((String) (i+" "+modele.getCurrentPlayer().objet.get(i).getCardName()));
            this.add(boutonCarte[i]);
            

        }
        caseText=new JTextField("2");
        this.add(caseText);
        utiliseButton= new JButton("Utiliser");
        utiliseButton.addActionListener(actionEvent -> {
            
            String res=caseText.getText().replaceAll("^\\s+$", "");
            try{
                int resint=Integer.parseInt(res);
                

                    
                    if(resint>modele.getCurrentPlayer().objet.size() || resint <0){
                        System.out.print("chiffre ne correspond pas");
                    }else{
                        
                        if(modele.nomSpecial.contains(modele.getCurrentPlayer().objet.get(resint).getCardName())){
                            
                            if(modele.getCurrentPlayer().objet.get(resint).getCardName() == "SacsDeSable"){
                                
                                modele.setSelectAction(Action.SacSable);
                            }else{
                                modele.setSelectAction(Action.Helico);
                            }
                            modele.getCurrentPlayer().objet.remove(resint);
                            if(modele.getCurrentPlayer().objet.size() <= 6){
                                modele.getCurrentPlayer().modeDefausse=0;
                            }
                            this.update();
                        }
                    }
                
            }catch(Exception e){
                System.out.print("mauvaise validation");
            }
            
        });
        this.add(utiliseButton);
        validateButton = new JButton("Defausse");
        validateButton.addActionListener(actionEvent -> {
            boolean canStart = true;
            String res=caseText.getText().replaceAll("^\\s+$", "");
            try{
                int resint=Integer.parseInt(res);
                

                    
                    if(resint>modele.getCurrentPlayer().objet.size() || resint <0){
                        System.out.print("chiffre ne correspond pas");
                    }else{
                        modele.getCurrentPlayer().objet.remove(resint);
                        this.update();
                    }
                
            }catch(Exception e){
                System.out.print("mauvaise validation");
            }
            
        });
        this.add(validateButton);
        String namebutton;
        if(echange){
            namebutton="choisissez carte";
        }else{
            namebutton="choix Joueur";
        }
        echangeButton = new JButton(namebutton);
            echangeButton.addActionListener(actionEvent -> {
                boolean canStart = true;
                String res=caseText.getText().replaceAll("^\\s+$", "");
                try{
                    int resint=Integer.parseInt(res);
                    

                        if(echange){
                            if(resint>3 || resint <0){
                                System.out.print("chiffre ne correspond pas joueur");
                            }else{
                                if(modele.getCurrentPlayer().getZone()==modele.getJoueurs(resint).getZone()){
                                    echange=false;
                                    joueurechange=resint;
                                    echangeButton.setText("choisissez carte");
                                    this.update();
                                }
                            }
                        }else{
                            if(resint>=modele.getCurrentPlayer().objet.size() || resint<0){
                                System.out.print("chiffre correspond pas carte");
                            }else{
                                modele.getJoueurs(joueurechange).objet.add(modele.getCurrentPlayer().objet.get(resint));
                                modele.getCurrentPlayer().objet.remove(resint);
                                if(modele.getCurrentPlayer().objet.size()<=6){
                                    modele.getCurrentPlayer().modeDefausse=0;
                                }
                                echangeButton.setText("choix Joueur");
                                echange=true;
                            }
                        }
                    
                }catch(Exception e){
                    System.out.print("mauvaise validation");
                }
                
            });
            this.add(echangeButton);
       
        
        
    }

    public void update() {
        
        if(!modele.victoire && !modele.defaite){
            for(int i=0;i<boutonCarte.length;i++){
                this.remove(boutonCarte[i]);
            }
            
            this.remove(caseText);
            this.remove(utiliseButton);
            this.remove(validateButton);
            
                this.remove(echangeButton);
            
            joueurLabel.setForeground(colorsJoueur[modele.getCurrentPlayer().getNumJoueur()]);
            joueurLabel.setText("Tour du joueur " +  (modele.getCurrentPlayer().getNumJoueur() + 1));
            actionLabel.setText("Actions dispos " + modele.getCurrentPlayer().getActionsRestantes() +"/3");
            artefactLabel.setText("" + modele.getCurrentPlayer().artefact());
            
            for(int i=0;i<Math.min(modele.getCurrentPlayer().objet.size(),6);i++){
                labels[i]=modele.getCurrentPlayer().objet.get(i).getCardName();
            }
            for(int i=modele.getCurrentPlayer().objet.size();i<6;i++){
                labels[i]="vide";
            }
            
            boutonCarte = new JLabel[modele.getCurrentPlayer().objet.size()];
            for(int i=0;i<modele.getCurrentPlayer().objet.size();i++){
                boutonCarte[i]=new JLabel((String) (i+" "+modele.getCurrentPlayer().objet.get(i).getCardName()));

                this.add(boutonCarte[i]);
                

            }
            caseText=new JTextField("2");
            this.add(caseText);
            utiliseButton= new JButton("Utiliser");
            utiliseButton.addActionListener(actionEvent -> {
                
                String res=caseText.getText().replaceAll("^\\s+$", "");
                try{
                    int resint=Integer.parseInt(res);
                    

                        
                        if(resint>modele.getCurrentPlayer().objet.size() || resint <0){
                            System.out.print("chiffre ne correspond pas");
                        }else{
                            
                            if( modele.nomSpecial.contains((String) modele.getCurrentPlayer().objet.get(resint).getCardName())){
                                
                                if(modele.getCurrentPlayer().objet.get(resint).getCardName() == "SacsDeSable"){
                                    
                                    modele.setSelectAction(Action.SacSable);
                                }else{
                                    modele.setSelectAction(Action.Helico);
                                }
                                modele.getCurrentPlayer().objet.remove(resint);
                                if(modele.getCurrentPlayer().objet.size()<=6){
                                    modele.getCurrentPlayer().modeDefausse=0;
                                }
                                this.update();
                            }
                        }
                    
                }catch(Exception e){
                    System.out.print("mauvaise validation");
                }
                
            });
            this.add(utiliseButton);
            validateButton = new JButton("Defausse");
            validateButton.addActionListener(actionEvent -> {
                
                String res=caseText.getText().replaceAll("^\\s+$", "");
                try{
                    int resint=Integer.parseInt(res);
                    

                        
                        if(resint>modele.getCurrentPlayer().objet.size() || resint <0){
                            System.out.print("chiffre ne correspond pas");
                        }else{
                            modele.getCurrentPlayer().objet.remove(resint);
                            if(modele.getCurrentPlayer().objet.size()<=6){
                                modele.getCurrentPlayer().modeDefausse=0;
                            }
                            this.update();
                        }
                    
                }catch(Exception e){
                    System.out.print("mauvaise validation");
                }
                
            });
            this.add(validateButton);
            String namebutton;
            if(echange){
                namebutton="choisissez carte";
            }else{
                namebutton="choix Joueur";
            }
            echangeButton = new JButton(namebutton);
            echangeButton.addActionListener(actionEvent -> {
                boolean canStart = true;
                String res=caseText.getText();
                try{
                    int resint=Integer.parseInt(res);
                    

                        if(!echange){
                            if(resint>4 || resint <0){
                                System.out.print("chiffre ne correspond pas joueur");
                            }else{
                                if(modele.getCurrentPlayer().getZone()==modele.getJoueurs(resint-1).getZone()){
                                    echange=true;
                                    joueurechange=resint-1;
                                    echangeButton.setText("choisissez carte");
                                    this.update();
                                }else{
                                    System.out.print("pas sur la meme case");
                                }
                            }
                        }else{
                            if(resint>=modele.getCurrentPlayer().objet.size() || resint<0){
                                System.out.print("chiffre correspond pas carte");
                            }else{
                                modele.getJoueurs(joueurechange).objet.add(modele.getCurrentPlayer().objet.get(resint));
                                modele.getCurrentPlayer().objet.remove(resint);
                                if(modele.getCurrentPlayer().objet.size()<=6){
                                    modele.getCurrentPlayer().modeDefausse=0;
                                }
                                echangeButton.setText("choix Joueur");
                                modele.getCurrentPlayer().decrementeAction();
                                echange=false;
                                this.update();
                            }
                        }
                    
                }catch(Exception e){
                    System.out.print("mauvaise validation");
                }
                
            });
            this.add(echangeButton);
        
        
                this.updateUI();
        
        }else{
            this.removeAll();
            String res;
            if(modele.victoire){
                res="victoire !";

            }else{
                res="defaite ...";
            }
            JLabel labelFin= new JLabel(res);
            this.add(labelFin);
            this.updateUI();
        }
    }

}