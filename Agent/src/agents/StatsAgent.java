package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.common.Node;
import platform.service.NameService;
import java.util.LinkedList;

public class StatsAgent extends AgentImpl {

    
    private int maxLinesToRead = 10;
    private long totalCount = 0;

    //si l'agent rentre sur son noeud de départ
    private boolean returning = false; 

    // File d'attente des étapes 
    private LinkedList<Node> itinerary = new LinkedList<>();

    public StatsAgent() {}
    public void setMaxLines(int max) { this.maxLinesToRead = max; }
    public void addDestination(Node n) { this.itinerary.add(n); }

    @Override
    public void main() throws MoveException {
        
        //cas où l'agent rentre
        if (returning) {
            // Si 'returning' est vrai, c'est qu'on vient d'exécuter back() et qu'on est arrivés.
            try {
                
                Class<?> clazz = Class.forName("apps.client.ClientCSV");//pour éviter de faire des imports et garder notre agent indépendant
                //charge la classe ClientMain en mémoire (dans clazz)
                java.lang.reflect.Field lockField = clazz.getField("lock");
                //on récupère le champ lock
                Object lock = lockField.get(null);
                //récupère la valeur du lock
                System.out.println("AGENT RENTRÉ AVEC SUCCÈS ! TOTAL = " + totalCount);
                
                synchronized (lock) {
                    lock.notify();
                }
                //prend le lock et reveille le client

            } catch (Exception e) {
                e.printStackTrace();
            }
            return; 
        }

        //cas où l'agent n'est pas en train de revenir
        System.out.println("[" + getName() + "] Je travaille sur " + getNameServer().toString()); 
        
        try {
            // On tente de récupérer le service
            Object serviceObj = getNameServer().get("NameService");//on récupère l'objet "NameService" dans l'annuaire
            if (serviceObj instanceof NameService) {
                NameService service = (NameService) serviceObj;
                long subTotal = 0;
                for (int i = 0; i < maxLinesToRead; i++) {
                    subTotal += service.getCountByLine(i);
                }
                //utilise le service récupéré
                totalCount += subTotal;
            } else {
                System.out.println("   -> Pas de NameService ici, je continue.");
            }
        } catch (Exception e) {
            System.err.println("   -> Erreur durant le travail : " + e.getMessage());
        }

        //navigation
        if (!itinerary.isEmpty()) {
            // S'il reste des étapes, on y va
            Node nextHop = itinerary.removeFirst();
            System.out.println("[" + getName() + "] Je pars vers l'étape suivante : " + nextHop.host);
            move(nextHop);
        } else {
            // Plus d'étapes
            System.out.println("[" + getName() + "] Itinéraire terminé. RETOUR BASE (" + getOrigin().host + ":" + getOrigin().port + ")");
            
            // On change l'état avant de partir
            this.returning = true;
            
            // On rentre à l'origine (définie par agent.init() dans ClientMain)
            back();
        }
    }
}