package modele;

import obsv.Observable;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
public class Modele extends Observable
{
    public static final int HAUTEUR=6, LARGEUR=6;
    public Zone[][] zones;
    public int niveauEau = 2;
    public final int NbJoueurs = 4;
    private Joueur[] joueurs;
    private Joueur currentPlayer;
    private Action ActionChoisie = Action.Rien;
    private Action ActionCarte = Action.Rien;
    public int carteChoisie=0;
    private Zone[] zoneInitPlayer;
    private List<Zone> zoneSpecialInit = new ArrayList<>(); 
    protected List<Artefact> artefacts = new ArrayList<>(); 
    protected List<Objet> objet = new ArrayList<>(); 
    public List<String> nomTuiles= new ArrayList<>();
    public List<String> nomTresors= new ArrayList<>();
    public List<String> nomSpecial= new ArrayList<>();
    private final Type[] typeSpecial = {Type.Feu, Type.Eau, Type.Air, Type.Terre}; 
    private ArrayList<Zone> pickable = new ArrayList<>();
    private final Random randomGenerator = new Random();
    public boolean victoire=false;
    public boolean defaite=false;

    public Modele() {
        zones = new Zone[LARGEUR][HAUTEUR]; 
        
        initSpecial();
        initGrille();

        zoneInitPlayer = new Zone[4];
        initPlayer();

        joueurs = new Joueur[NbJoueurs];
        for (int k = 0; k< NbJoueurs; k++) {
            joueurs[k] = new Joueur(this);
        }
        tourJoueur(0); 
    }
    private void initSpecial(){
        nomSpecial.add("SacsDeSable");
        nomSpecial.add("Helicoptère");
    }
    private void initTresors(){
        nomTresors.add("LaStatueduzéphyr");
        nomTresors.add("LeCalicedelonde");
        nomTresors.add("LeCristalardent");
        nomTresors.add("LaPierresacrée");

    }
    private void initNomTuiles(){
        nomTuiles.add("Heliport");
        nomTuiles.add("LaCaverneDesOmbres");
        nomTuiles.add("LaCaverneDuBrasier");
        nomTuiles.add("LaForetPourpre");
        nomTuiles.add("LaPortedArgent");
        nomTuiles.add("LaPorteDeBronze");
        nomTuiles.add("LaPorteDeCuivre");
        nomTuiles.add("LaPorteDeFer");
        nomTuiles.add("LaPortedOr");
        nomTuiles.add("LaTourDeGuet");
        nomTuiles.add("LeJardinDesHurlements");
        nomTuiles.add("LeJardinDesMurmures");
        nomTuiles.add("LeLagonPerdu");
        nomTuiles.add("LeMaraisBrumeux");
        nomTuiles.add("LePalaisDeCorail");
        nomTuiles.add("LePalaisDesMarees");
        nomTuiles.add("LePontDesAbimes");
        nomTuiles.add("LeRocherFantome");
        nomTuiles.add("LesDunesDeLIllusion");
        nomTuiles.add("LesFalaisesDeLOubli");
        nomTuiles.add("LeTempleDeLaLune");
        nomTuiles.add("LeTempleDuSoleil");
        nomTuiles.add("LeValDuCrepuscule");
        nomTuiles.add("Observatoire");



    }
    private void initGrille(){
        initNomTuiles();
        initTresors();
        Collections.shuffle(nomTuiles);
        Collections.shuffle(nomTresors);
        //System.out.print(nomTuiles.size());
        int tr=0;
        int j =0;
        int i =0;
        int nbzones=0;
        for(i=0;i<6;i++){

            for(j=0;j<6;j++){
                
                if((i==0 && j==0)|| (i==0 && j==5) || (i==5 && j==0) || (i==5 && j==5)){
                    zones[i][j]=new Zone(this,i,j,Type.Vide,Etat.Normal);
                    zones[i][j].setTuile(nomTresors.get(tr));
                    //System.out.print(nomTresors.get(tr));
                    //System.out.print(tr);
                    tr++;
                }
                else if ((i==0 && j==1) || (i==0 && j==4) || (i==1 && j==0) || (i==1 && j==5) || (i==4 && j==0) || (i==4 && j==5) || (i==5 && j==1) || (i==5 && j==4)) {
                    zones[i][j]=new Zone(this,i,j,Type.Vide,Etat.Submergee);
                    //zones[i][j].etat=Etat.Submergee;
                    //System.out.print(zones[i][j].getEtat());
                }
                else{
                    zones[i][j]= new Zone(this,i,j,Type.Normal,Etat.Normal);
                    zones[i][j].setTuile(nomTuiles.get(nbzones));
                    
                    nbzones++;
                }
                
                //System.out.print(nbzones);
                
            }
            
            
        }
    }

   
    private void initPlayer(){
        zoneInitPlayer[0] = this.getZone(1, 1);
        zoneInitPlayer[1] = this.getZone(1, 4);
        zoneInitPlayer[2] = this.getZone(4, 1);
        zoneInitPlayer[3] = this.getZone(4, 4);
    }

   

    
    



    
    private void nonSub(ArrayList<Zone> res) {
    	
        for(Zone[] c : this.zones) {
            for(Zone z : c) {
                if ((z.etat != Etat.Submergee) && (z.getType()!=Type.Vide)) res.add(z);
            }
        }
        
    }

    private Zone zoneAleatoire() {
    	if(pickable.size()==0) {
    		nonSub(pickable);
    	}
        int rand = randomGenerator.nextInt(pickable.size());
        Zone z=pickable.get(rand);
        pickable.remove(rand);
        return z;
    }

    public void setSelectAction(Action action){
        this.ActionChoisie = action;
    }

 
    public void tourSuivant() {
        Zone s1,s2,s3 = null,s4=null,s5=null;
        s1 = zoneAleatoire();
        s2 = zoneAleatoire();
        if(niveauEau>2){
            s3 = zoneAleatoire();
            if(niveauEau>3){
                s4 = zoneAleatoire();
                if(niveauEau>4){
                    s5 = zoneAleatoire();
                }
            }
        }
        for(Zone[] c : this.zones) {
            for(Zone z : c) {
                if(niveauEau==2){
                    z.evalue(s1, s2);
                }else if(niveauEau==3){
                    z.evalue(s1, s2, s3);
                }else if(niveauEau==4){
                    z.evalue(s1, s2, s3,s4);
                }else if(niveauEau==5){
                    z.evalue(s1, s2, s3,s4,s5);
                }
                    
                
                
                z.evolue();
            }
        }
        victoire=winCondition();
        defaite=defeatCondition();

        

        tourJoueur((currentPlayer.getNumJoueur() + 1)%4);
        
        notifyObservers();
    }

    
    public void tourJoueur(int i){
        
        currentPlayer = joueurs[i];
        if(currentPlayer.objet.size()>=5){
            currentPlayer.modeDefausse=1;

        }
        ArrayList<Card> pioche = Pioche(2);
        for(int j=0;j<pioche.size();j++){
            currentPlayer.objet.add(pioche.get(j));
        }

        System.out.println("Tour du joueur : " + currentPlayer.getNumJoueur()+1);
        currentPlayer.debutTour();
    }

    public void actionJoueur(Zone cible){
        System.out.println("Emplacement joueur " + currentPlayer.getZone().getX() + " " + currentPlayer.getZone().getY());
        System.out.println("Zone cible : " + cible.getX() + " " + cible.getY());
        System.out.println("Artefact ? " + cible.getContainArtefact());
        System.out.println("etat ? " + cible.getEtat());
        System.out.println("etat ? " + zones[0][2].getEtat());
        int i=cible.getX();
        int j = cible.getY();
        if (currentPlayer.getActionsRestantes() != 0 && (ActionChoisie!=Action.SacSable || ActionChoisie!=Action.Helico) && currentPlayer.modeDefausse!=1) {
            
            switch(ActionChoisie){
                case Deplacement:
                    if (zones[i][j].getEtat() != Etat.Submergee && currentPlayer.getZone().trouveAdjacentes().contains(cible) ) {
                        currentPlayer.deplaceJoueur(cible);
                        currentPlayer.decrementeAction();
                        ActionChoisie = Action.Rien;
                    } else
                        System.out.println("Déplacement Invalide");
                    break;
                case Seche:
                    if (currentPlayer.getZone().trouveAdjacentes().contains(cible) && cible.etat == Etat.Inondee) {
                        cible.asseche();
                        currentPlayer.decrementeAction();
                        ActionChoisie = Action.Rien;
                    } else
                        System.out.println("Cette zone ne peut être assechée");
                    break;
                case Artefact:
                    System.out.print(currentPlayer.getZone().getTuile());
                    if((currentPlayer.getZone().getTuile() == "LaCaverneDesOmbres" || currentPlayer.getZone().getTuile() == "LaCaverneDuBrasier") && (currentPlayer.compteOccurenceCarte("LeCristalardent")>=4)){
                        currentPlayer.pickUpArtefact("LeCristalardent");
                        ActionChoisie = Action.Rien;
                    } 
                    else if((currentPlayer.getZone().getTuile() == "LeJardinDesHurlements" || currentPlayer.getZone().getTuile() == "LeJardinDesMurmures") && (currentPlayer.compteOccurenceCarte("LaStatueduzéphyr")>=4)){
                        currentPlayer.pickUpArtefact("LaStatueduzéphyr");
                        ActionChoisie = Action.Rien;
                    }else if((currentPlayer.getZone().getTuile() == "LePalaisDeCorail" || currentPlayer.getZone().getTuile() == "LePalaisDesMarees") && (currentPlayer.compteOccurenceCarte("LeCalicedelonde")>=4)){
                        currentPlayer.pickUpArtefact("LeCalicedelonde");
                        ActionChoisie = Action.Rien;
                    }else if((currentPlayer.getZone().getTuile() == "LeTempleDeLaLune" || currentPlayer.getZone().getTuile() == "LeTempleDuSoleil") && (currentPlayer.compteOccurenceCarte("LaPierresacrée")>=4)){
                        currentPlayer.pickUpArtefact("LaPierresacrée");
                        ActionChoisie = Action.Rien;
                    }
                    else{
                        System.out.println("Il n'y a pas d'artefact sur cette zone");
                    }
                    break;

                case SacSable:
                    if (cible.etat == Etat.Inondee) {
                        cible.asseche();
                        
                        ActionChoisie = Action.Rien;
                    } else
                        System.out.println("Cette zone ne peut être assechée");
                    break;
                case Helico:
                    if (zones[i][j].getEtat() != Etat.Submergee ) {
                        currentPlayer.deplaceJoueur(cible);
                        
                        ActionChoisie = Action.Rien;
                    } else
                        System.out.println("Déplacement Invalide");
                    break;
                default: System.out.print("Selectionnez une action valide");

                if(ActionChoisie==Action.SacSable){
                    if (cible.etat == Etat.Inondee) {
                        cible.asseche();
                        
                        ActionChoisie = Action.Rien;
                    } else
                        System.out.println("Cette zone ne peut être assechée");

                }
            }
        } else {
            System.out.print(" Vous ne pouvez plus effectuer d'action.");
            ActionChoisie=Action.Rien;
        }
        System.out.print("\n Emplacement nouveau " + currentPlayer.getZone().getX()+ " " + currentPlayer.getZone().getY() + "\n");
        notifyObservers();
    }

    public Zone getHeliport(){
        for(Zone[]c : zones)
            for(Zone z : c)
                if(z.getType() == Type.Heliport)
                    return z;
                return null;
    }

    
    public ArrayList<Card> Pioche(int nbcartes){
        ArrayList<Card> pioche = new ArrayList<>();
        double rand ;
        for(int i=0;i<2;i++){
            rand=Math.random();
            if (rand <=0.5){
                pioche.add( new Card(nomTresors.get(randomGenerator.nextInt(nomTresors.size()))));
            }else if (rand <=0.97){
                pioche.add(new Card(nomSpecial.get(randomGenerator.nextInt(nomSpecial.size()))));

            }else{
                System.out.print("Le niveau des eaux monte il est à present à : "+ (niveauEau+1)+"\n");
                niveauEau++;
            }
        }
        
        return pioche;

    }

    public List<Zone> getZoneSpecialInit() { return zoneSpecialInit;}

    public Zone getZoneInitPlayer(int index){ return zoneInitPlayer[index]; }

    public Joueur getJoueurs(int i){
        return joueurs[i];
    }

    public Zone getZone(int i, int j) {
        return zones[i][j];
    }

    public Joueur getCurrentPlayer(){ return currentPlayer; }

    public boolean recupArtefact(String artef){
        for(Joueur j : joueurs){
            if(j.artefact.contains(artef)){
                return true;
            }
        }
        return false;
    }

    public boolean carteHelico(){
        for(Joueur j : joueurs){
            if(j.objet.contains("Helicoptère")){
                return true;
            }
        }
        return false;
    }
    public boolean artefactHelicoPerdu(){
        int nbCarteCristalSub=0;
        int nbCarteStatueSub=0;
        int nbCartePierreSub=0;
        int nbCarteCaliceSub=0;
        for(int i =0;i<6;i++){
            for(int j=0;j<6;j++){
                if((zones[i][j].getTuile() == "LaCaverneDesOmbres" || zones[i][j].getTuile() == "LaCaverneDuBrasier" ) && zones[i][j].getEtat() == Etat.Submergee){
                    nbCarteCristalSub++;
                }
                if((zones[i][j].getTuile() == "LeJardinDesHurlements" || zones[i][j].getTuile() == "LeJardinDesMurmures" ) && zones[i][j].getEtat() == Etat.Submergee){
                    nbCarteStatueSub++;
                }
                if((zones[i][j].getTuile() == "LePalaisDeCorail" || zones[i][j].getTuile() == "LePalaisDesMarees" ) && zones[i][j].getEtat() == Etat.Submergee){
                    nbCarteCaliceSub++;
                }
                if((zones[i][j].getTuile() == "LeTempleDeLaLune" || zones[i][j].getTuile() == "LeTempleDuSoleil" ) && zones[i][j].getEtat() == Etat.Submergee){
                    nbCartePierreSub++;
                }
                if(zones[i][j].getTuile()=="Heliport" && zones[i][j].getEtat() == Etat.Submergee){
                    return true;
                }
            }
            
        }
        return ((nbCartePierreSub>=2) || (nbCarteCristalSub>=2) || (nbCarteCaliceSub>=2) || (nbCarteStatueSub>=2));
    }

    public boolean joueurPiege(){
        for(Joueur j : joueurs){
            if(j.getZone().getEtat() == Etat.Submergee ){
                List<Zone> zoneAdj = j.getZone().trouveAdjacentes();
                boolean sauvetage=false;
                for(int i=0;i<zoneAdj.size() && sauvetage!=true;i++){
                    if(zoneAdj.get(i).getEtat() != Etat.Submergee){
                        sauvetage=true;
                    }
                }
                if(!sauvetage && !(j.objet.contains("Helicoptère"))){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean defeatCondition(){
        return (joueurPiege() || artefactHelicoPerdu()) ;
    } 
    public boolean winCondition(){
        if( recupArtefact("LaPierresacrée") && recupArtefact("LaStatueduzéphyr") && recupArtefact("LeCristalardent") && recupArtefact("LeCalicedelonde") && carteHelico()){
            return true;
        }
        return false;
    }

    
}
