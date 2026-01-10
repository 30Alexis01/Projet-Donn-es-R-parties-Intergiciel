package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;

/**
 * Un agent qui fait un aller-retour.
 * Départ (Client) -> Serveur -> Retour (Client)
 */
public class PingPongAgent extends AgentImpl {

    // État de l'agent (sera sérialisé avec lui)
    private boolean hasVisitedServer = false;

    @Override
    public void main() throws MoveException {
        // On utilise getName() qui vient de AgentImpl
        System.out.println("[" + getName() + "] Exécution en cours...");

        if (!hasVisitedServer) {
            System.out.println("[" + getName() + "] Je suis sur le Serveur (ou en route).");
            System.out.println("[" + getName() + "] Je travaille un peu... (Simulation)");
            
            try { 
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            hasVisitedServer = true;
            System.out.println("[" + getName() + "] Travail terminé, je rentre à la maison (back) !");
            
            // Retour à l'envoyeur
            back(); 
            
        } else {
            System.out.println("[" + getName() + "] Je suis revenu sur le Client !");
            System.out.println("[" + getName() + "] Mission accomplie.");
        }
    }
}