package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.common.Node;
import platform.service.NameService;
import java.util.LinkedList;

public class StatsAgent extends AgentImpl {

    // --- CONFIG ---
    private int maxLinesToRead = 10;
    
    // --- ÉTAT ---
    private long totalCount = 0;
    
    // NOUVEL ÉTAT : Permet de savoir si on vient d'arriver à la maison
    private boolean returning = false; 

    // File d'attente des étapes SUIVANTES
    private LinkedList<Node> itinerary = new LinkedList<>();

    public StatsAgent() {}
    public void setMaxLines(int max) { this.maxLinesToRead = max; }
    public void addDestination(Node n) { this.itinerary.add(n); }

    @Override
    public void main() throws MoveException {
        
        // ==================================================
        // PHASE 1 : ARRIVÉE À LA MAISON (FIN DE MISSION)
        // ==================================================
        if (returning) {
            // Si 'returning' est vrai, c'est qu'on vient d'exécuter back() et qu'on est arrivés.
            try {
                // On réveille le ClientMain via Réflexion
                Class<?> clazz = Class.forName("apps.client.ClientMain");
                java.lang.reflect.Field lockField = clazz.getField("lock");
                Object lock = lockField.get(null);
                
                System.out.println(">>> AGENT RENTRÉ AVEC SUCCÈS ! TOTAL = " + totalCount);
                
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return; // Arrêt complet de l'agent
        }

        // ==================================================
        // PHASE 2 : TRAVAIL (Sur un serveur distant)
        // ==================================================
        System.out.println("[" + getName() + "] Je travaille sur " + getNameServer().toString()); // Debug simple
        
        try {
            // On tente de récupérer le service, mais on ne plante pas si absent
            Object serviceObj = getNameServer().get("NameService");
            if (serviceObj instanceof NameService) {
                NameService service = (NameService) serviceObj;
                long subTotal = 0;
                for (int i = 0; i < maxLinesToRead; i++) {
                    subTotal += service.getCountByLine(i);
                }
                totalCount += subTotal;
            } else {
                System.out.println("   -> Pas de NameService ici, je continue.");
            }
        } catch (Exception e) {
            System.err.println("   -> Erreur durant le travail : " + e.getMessage());
        }

        // ==================================================
        // PHASE 3 : NAVIGATION
        // ==================================================
        if (!itinerary.isEmpty()) {
            // S'il reste des étapes, on y va
            Node nextHop = itinerary.removeFirst();
            System.out.println("[" + getName() + "] Je pars vers l'étape suivante : " + nextHop.host);
            move(nextHop);
        } else {
            // Plus d'étape ? C'est l'heure de rentrer.
            System.out.println("[" + getName() + "] Itinéraire terminé. RETOUR BASE (" + getOrigin().host + ":" + getOrigin().port + ")");
            
            // IMPORTANT : On change l'état AVANT de partir
            this.returning = true;
            
            // On rentre à l'origine (définie par agent.init() dans ClientMain)
            back();
        }
    }
}