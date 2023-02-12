package modele;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zone {
    private Modele modele;
    private String tuile="";
    protected Etat etat;
    protected Type type;
    private final int x, y;
    private boolean occupee;
    protected List<Joueur> joueursOn;
    private boolean containsArtefact = false;
    private Artefact artefact = null;

    public void setTuile(String t) {
    	tuile=t;
    }
    public String getTuile(){
        return tuile;
    }
    public Zone(Modele m, int i, int j, Type t,Etat et){
        this.modele = m;
        this.x = i;
        this.y = j;
        this.etat = et;
        this.type = t;
        this.joueursOn = new ArrayList<>();
        this.occupee = false;
    }

    public Joueur[] getJoueur(){
        Joueur[] res = new Joueur[joueursOn.size()];
        for (int i = 0; i<joueursOn.size(); i++){
            res[i] = joueursOn.get(i);
        }
        return res;
    }

    public Type getType(){ return this.type; }
    public int getX() {return this.x;}
    public int getY() {return this.y;}


    public void putJoueur(Joueur j){
        this.joueursOn.add(j);
        this.occupee = true;
    }

    public void removeJoueur(Joueur j){
        if(joueursOn.contains(j)){
            joueursOn.remove(j);
            if(joueursOn.isEmpty()) occupee = false;
        }
    }

    public boolean hasJoueurOn(){ return this.occupee; }

    private Etat etatSuivant;

    public void inonde() {
        if (this.etat == Etat.Inondee) this.etatSuivant = Etat.Submergee ;
        else if (this.etat == Etat.Normal) this.etatSuivant = Etat.Inondee;
    }

    public void asseche(){
        this.etat = Etat.Normal;
    }

    protected void evalue(Zone s1, Zone s2, Zone s3,Zone s4,Zone s5) {
        if ((this == s1) || (this == s2) || (this == s3) || (this == s4 ) || (this == s5)) inonde();
        else this.etatSuivant = etat;
    }

    protected void evalue(Zone s1, Zone s2, Zone s3,Zone s4) {
        if ((this == s1) || (this == s2) || (this == s3) || (this == s4 )) inonde();
        else this.etatSuivant = etat;
    }

    protected void evalue(Zone s1, Zone s2, Zone s3) {
        if ((this == s1) || (this == s2) || (this == s3)) inonde();
        else this.etatSuivant = etat;
    }

    protected void evalue(Zone s1, Zone s2) {
        if ((this == s1) || (this == s2)) inonde();
        else this.etatSuivant = etat;
    }

    protected void evolue() {
        this.etat = this.etatSuivant;
    }

    protected List<Zone> trouveAdjacentes(){
        List<Zone> ar=new ArrayList<>();
        if(x+1<6){
            ar.add(modele.getZone(x+1,y));
        }
        if(y-1>=0){
            ar.add(modele.getZone(x,y-1));
        }
        if(x-1>=0){
            ar.add(modele.getZone(x-1,y));
        }
        if(y+1<6){
            ar.add(modele.getZone(x,y+1)) ;
        }
        return ar;
    }

    public Etat getEtat() {
        return this.etat;
    }

    public void setEtat(Etat etat){ this.etat = etat;}


    public void setArtefact(Artefact artefact){
        this.containsArtefact = true;
        this.artefact = artefact;
        this.type = this.artefact.getType();
        modele.artefacts.remove(artefact);
    }

    public Artefact pickUpArtefact(){
        this.containsArtefact = false;
        this.artefact.setPickedUp(true);
        return this.artefact;
    }


    public Etat getSuiv() {
        return this.etatSuivant;
    }

    public boolean getContainArtefact(){ return this.containsArtefact;}


    public Artefact getArtefact() { return this.artefact; }
}
