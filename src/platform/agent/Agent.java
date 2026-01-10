package platform.agent;

import java.io.Serializable;
import java.util.Hashtable;

import platform.common.MoveException;
import platform.common.Node;

/**
 * Interface Agent (comme dans le poly).
 * Un agent est Serializable (on migre son état).
 *
 * Remarque : "main()" = la méthode relancée à chaque arrivée (migration faible).
 */
public interface Agent extends Serializable {

    /** Initialisation de base : nom + node d'origine (pour back()) */
    void init(String name, Node origin);

    /** Donne l'annuaire local (services accessibles sur le node) */
    void setNameServer(Hashtable<String, Object> ns);

    /** Récupère l'annuaire local */
    Hashtable<String, Object> getNameServer();

    /** Déplace l'agent vers un node cible */
    void move(Node target) throws MoveException;

    /** Retourne sur le node d'origine */
    void back() throws MoveException;

    /** Corps principal de l'agent (appelé/re-démarré à chaque migration) */
    void main() throws MoveException;
}
