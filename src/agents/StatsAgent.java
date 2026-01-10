package agents;

import platform.agent.AgentImpl;
import platform.common.MoveException;
import platform.service.NameService;

public class StatsAgent extends AgentImpl {

    // Paramètres de la mission
    private String targetName;
    private int startYear;
    private int endYear;

    // Résultat rapporté
    private int totalCount = 0;
    
    // État interne pour savoir si on a fini
    private boolean hasFinished = false;

    // Constructeur vide requis pour la désérialisation
    public StatsAgent() {}

    // Méthode helper pour configurer l'agent avant le départ
    public void setMission(String targetName, int startYear, int endYear) {
        this.targetName = targetName;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    @Override
    public void main() throws MoveException {
        // Si on a fini (donc on est de retour chez le client), on affiche le résultat
        if (hasFinished) {
            System.out.println("---------------------------------------------");
            System.out.println("Rapport de l'Agent " + getName() + " :");
            System.out.println("Le prénom " + targetName + " a été donné " + totalCount + " fois");
            System.out.println("entre " + startYear + " et " + endYear + ".");
            System.out.println("---------------------------------------------");
            return;
        }

        // Sinon, on est sur le Serveur : Au boulot !
        System.out.println("[" + getName() + "] Je commence l'analyse pour '" + targetName + "'...");
        
        // 1. Récupérer le service local
        NameService service = (NameService) getNameServer().get("NameService");
        
        if (service == null) {
            System.err.println("[" + getName() + "] Erreur : Service 'NameService' introuvable !");
        } else {
            // 2. La boucle "Une par une" (Localement, c'est très rapide)
            long start = System.currentTimeMillis();
            
            for (int year = startYear; year <= endYear; year++) {
                int count = service.getCount(year, targetName);
                // System.out.println(" - " + year + " : " + count); // Décommente si tu veux du verbeux
                totalCount += count;
            }
            
            long end = System.currentTimeMillis();
            System.out.println("[" + getName() + "] Analyse terminée en " + (end - start) + "ms. Résultat provisoire : " + totalCount);
        }

        // 3. On note qu'on a fini et on rentre
        hasFinished = true;
        System.out.println("[" + getName() + "] Je rentre à la base...");
        back();
    }
}