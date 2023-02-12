package controleur;

import modele.Action;
import modele.Modele;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {
    enum Direction {
        haut, bas, gauche, droit
    }

    Modele modele;
    private Action action;

    public Controller(Modele modele, Action action) {
        
        
        this.modele = modele;
        this.action = action;

    }

    public void actionPerformed(ActionEvent e) {
        if(modele.getCurrentPlayer().modeDefausse!=1){
            if (action == Action.ProchainTour) {
                
                modele.tourSuivant();

            } else {
                modele.setSelectAction(action);
            }
        }
    }
}