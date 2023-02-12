package modele;

public class Artefact {

    private Modele modele;
    private final Type type;
    private boolean isPickedUp = false;
    private Joueur pickedUpBy = null;

    public Artefact(Modele modele, Type type){
        this.modele = modele;
        this.type = type;
    }
    
    public void setPickedUp(boolean pickedUp) { isPickedUp = pickedUp;}

    public void setPickedUpBy(Joueur j){ pickedUpBy = j;}

    public Type getType(){ return this.type; }

    public String tostring(){
       return (""+ type );
    }

    
}
