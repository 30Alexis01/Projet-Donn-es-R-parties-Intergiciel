package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.common.Node;
import platform.service.NameService;
import java.util.LinkedList;

public class StatsAgent extends AgentImpl {

    private int maxLinesToRead = 10;
    private long totalCount = 0;
    private boolean isFinished = false;
    private LinkedList<Node> itinerary = new LinkedList<>();

    public StatsAgent() {}
    public void setMaxLines(int max) { this.maxLinesToRead = max; }
    public void addDestination(Node n) { this.itinerary.add(n); }

    @Override
    public void main() throws MoveException {
        // CAS 1 : RETOUR À LA MAISON (FIN)
        if (isFinished) {
            // On ne print rien pour ne pas polluer le CSV
            // On signale au ClientMain qu'on est arrivé via le verrou
            try {
                // Utilisation de la Réflexion pour éviter les erreurs de compilation/linkage sur le serveur
                Class<?> clazz = Class.forName("apps.client.ClientMain");
                java.lang.reflect.Field lockField = clazz.getField("lock");
                Object lock = lockField.get(null); // accès au champ static
                
                synchronized (lock) {
                    lock.notify(); // RÉVEIL DU CLIENT !
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // CAS 2 : TRAVAIL (identique avant)
        NameService service = (NameService) getNameServer().get("NameService");
        if (service != null) {
            for (int i = 0; i < maxLinesToRead; i++) {
                totalCount += service.getCountByLine(i);
            }
        }

        // CAS 3 : NAVIGATION (identique avant)
        if (!itinerary.isEmpty()) {
            Node nextHop = itinerary.removeFirst();
            move(nextHop);
        } else {
            isFinished = true;
            back();
        }
    }
}