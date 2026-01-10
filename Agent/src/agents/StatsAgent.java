package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.service.NameService;

public class StatsAgent extends AgentImpl {

    // On change les paramètres : juste combien de lignes on veut lire
    private int maxLinesToRead = 10000;
    
    private long totalCount = 0; // long pour éviter l'overflow si ça devient énorme
    private boolean hasFinished = false;
    private long executionTime = 0; // Pour mesurer le temps sur place

    public StatsAgent() {}

    public void setMaxLines(int max) {
        this.maxLinesToRead = max;
    }

    @Override
    public void main() throws MoveException {
        if (hasFinished) {
            System.out.println("---------------------------------------------");
            System.out.println("Rapport de l'Agent " + getName() + " :");
            System.out.println("Somme des naissances sur les " + maxLinesToRead + " premières lignes.");
            System.out.println("Total : " + totalCount);
            System.out.println("Temps de calcul interne (Serveur) : " + executionTime + " ms");
            System.out.println("---------------------------------------------");
            return;
        }

        System.out.println("[" + getName() + "] Je commence le calcul intensif (" + maxLinesToRead + " appels)...");
        
        NameService service = (NameService) getNameServer().get("NameService");
        
        if (service != null) {
            long start = System.currentTimeMillis();
            
            // LA BOUCLE INFERNALE (10 000 tours)
            for (int i = 0; i < maxLinesToRead; i++) {
                // Appel local (extrêmement rapide)
                totalCount += service.getCountByLine(i);
            }
            
            executionTime = System.currentTimeMillis() - start;
            System.out.println("[" + getName() + "] Terminé en " + executionTime + "ms.");
        }

        hasFinished = true;
        back();
    }
}