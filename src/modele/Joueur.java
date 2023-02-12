package modele;

import obsv.Observable;

import java.util.ArrayList;
import java.util.List;

public class Joueur extends Observable {
    private Modele modele;
    private Zone zoneCourante; 
    private int actionsRestantes = 3;
    private final int numJoueur; 
    private static int nbJoueurs = 0; 
    public List<String> artefact = new ArrayList<>();
    public List<Card> objet = new ArrayList<>();
    
    public int modeDefausse=0;

    public Joueur(Modele modele) {
        this.modele = modele;
        numJoueur = nbJoueurs; 
        nbJoueurs ++; 
        zoneCourante = modele.getZoneInitPlayer(numJoueur);
        zoneCourante.putJoueur(this);
    }

    public void deplaceJoueur (Zone z) {
        this.zoneCourante.removeJoueur(this);
        this.zoneCourante = z;
        z.putJoueur(this);
    }


    public void debutTour() {
        this.actionsRestantes = 3;
    }

    public void decrementeAction() {
        this.actionsRestantes--;
    }

    public void pickUpArtefact(String s){
        this.artefact.add(s);
        int nbs=this.compteOccurenceCarte(s);
        for(int i=0;i<nbs;i++) {
        	removeObjet(s);
        }
        System.out.print("ca marche tema + " + s);
        decrementeAction();
    }
    
    public void removeObjet(String s) {
    	for(int i=0;i<this.objet.size();i++) {
    		if(this.objet.get(i).getCardName()==s) {
    			this.objet.remove(i);
    		}
    	}
    }
    

    public Zone getZone() {return zoneCourante;}

    public int getNumJoueur() { return numJoueur ; }

    public int indexJoueurSuivant() {
        if (numJoueur == nbJoueurs - 1) return 0;
        else return numJoueur+1;
    }

    public String artefact(){
        String s="artefacts : ";
        for(int i=0;i<artefact.size();i++){
            s+=""+artefact.get(i)+", ";
        }
        return s;
    }
    public int compteOccurenceCarte(String nom){
        int n=0;
        for(Card c : objet){
            if(c.getCardName() == nom){
                n++;
            }
        }
        return n;
    }
    /*
    public String objets() {
    	String s= "Objets : \n";
        for(Objet b : objet){
            s+= "" + b.getType() + ",";
        }
        return s;
    }*/
    
    public int getActionsRestantes(){ return this.actionsRestantes; }
}