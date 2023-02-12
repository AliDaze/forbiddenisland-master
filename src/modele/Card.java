package modele;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Card {
    private String name;
    private TypeCartes type;
    List<String> CartesArtefacts=new ArrayList<String>() {{
	add("LaStatueduzéphyr");
	add("LeCalicedelonde");
	add("LeCristalardent");
	add("LaPierresacrée");}};

    public Card(String name) {
        this.name = name;
        if(CartesArtefacts.contains(name)){
            this.type=TypeCartes.Artefactc;
        }else{
            this.type=TypeCartes.Speciale;
        }
    }


    public String getCardName() {
        return this.name;
    }
}