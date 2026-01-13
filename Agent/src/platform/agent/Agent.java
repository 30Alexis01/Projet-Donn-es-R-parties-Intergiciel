package platform.agent;

import java.io.Serializable;
import java.util.Hashtable;

import platform.common.MoveException;
import platform.common.Node;

//Interface Agent 
public interface Agent extends Serializable {

    // Initialisation 
    void init(String name, Node origin);

    //Donne l'annuaire local 
    void setNameServer(Hashtable<String, Object> ns);

    //Récupère l'annuaire local
    Hashtable<String, Object> getNameServer();

    // Déplace l'agent vers un node cible
    void move(Node target) throws MoveException;

    //Retourne sur le node d'origine
    void back() throws MoveException;

    //Corps principal de l'agent 
    void main() throws MoveException;
}
