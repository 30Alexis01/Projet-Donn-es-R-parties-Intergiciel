package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.common.Node;
import platform.service.NameService;
import java.util.LinkedList;

public class StatsAgent extends AgentImpl {

    // --- PARAMÈTRES ---
    private int maxLinesToRead = 10000;
    
    // --- ÉTAT ---
    private long totalCount = 0;       // La somme cumulée
    private boolean isFinished = false; // Pour savoir quand afficher le résultat

    // --- ITINÉRAIRE ---
    // Liste des prochains sauts (File d'attente)
    private LinkedList<Node> itinerary = new LinkedList<>();

    public StatsAgent() {}

    // Configuration
    public void setMaxLines(int max) { this.maxLinesToRead = max; }
    
    public void addDestination(Node n) { this.itinerary.add(n); }

    @Override
    public void main() throws MoveException {
        // CAS 1 : RETOUR À LA MAISON
        if (isFinished) {
            System.out.println("---------------------------------------------");
            System.out.println("Rapport Final de " + getName());
            System.out.println("Mission accomplie sur " + (itinerary.size() + 2) + " sauts."); // +2 pour aller/retour
            System.out.println("TOTAL CUMULÉ : " + totalCount);
            System.out.println("---------------------------------------------");
            return;
        }

        // CAS 2 : TRAVAIL SUR LE SERVEUR ACTUEL
        System.out.println("[" + getName() + "] Arrivé ! Je travaille...");
        NameService service = (NameService) getNameServer().get("NameService");

        if (service != null) {
            long subTotal = 0;
            for (int i = 0; i < maxLinesToRead; i++) {
                subTotal += service.getCountByLine(i);
            }
            totalCount += subTotal;
            System.out.println("[" + getName() + "] Sous-total ici : " + subTotal);
        } else {
            System.err.println("[" + getName() + "] Pas de NameService ici !");
        }

        // CAS 3 : DÉCISION DE NAVIGATION
        if (!itinerary.isEmpty()) {
            // S'il reste une destination, on la prend et on y va
            Node nextHop = itinerary.removeFirst();
            System.out.println("[" + getName() + "] Hop ! Je pars vers " + nextHop.host);
            move(nextHop);
        } else {
            // Plus de destination, on rentre à la base (origin)
            System.out.println("[" + getName() + "] Tournée finie. Je rentre à la base.");
            isFinished = true;
            back();
        }
    }
}