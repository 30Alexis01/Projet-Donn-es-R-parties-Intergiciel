package apps.server;

import platform.server.AgentServer;
import platform.service.NameService;

/**
 * Point d'entrée du serveur "Agents".
 * Lance uniquement :
 *  - un AgentServer
 *  - avec le service NameService (CsvNameService) dans l'annuaire
 *
 * Configuration via attributs ci-dessous (pas via args).
 */
public class ServerMain {

    // =========================
    // CONFIG A MODIFIER ICI
    // =========================

    // IP d'écoute du serveur (0.0.0.0 = toutes les interfaces, recommandé)
    private static final String LISTEN_IP = "0.0.0.0";

    // Port d'écoute du serveur d'agents
    private static final int AGENT_PORT = 2000;

    // Chemin du CSV (relatif à l'endroit où tu lances java)
    private static final String CSV_PATH = "prenoms.csv";

    // Nom utilisé dans le NameServer
    private static final String SERVICE_NAME = "NameService";

    // =========================

    public static void main(String[] args) {
        try {
            System.out.println("=== Démarrage du serveur Agents ===");
            System.out.println("Listen IP : " + LISTEN_IP);
            System.out.println("Port      : " + AGENT_PORT);
            System.out.println("CSV       : " + CSV_PATH);

            // 1) Charger le service (CSV)
            NameService service = new CsvNameService(CSV_PATH);

            // 2) Démarrer le serveur d'agents
            AgentServer server = new AgentServer(LISTEN_IP, AGENT_PORT);

            // 3) Injecter le service dans l'annuaire
            server.getNameServer().put(SERVICE_NAME, service);

            // 4) Lancer
            server.start();
            System.out.println(">> [Agent] Serveur prêt sur " + LISTEN_IP + ":" + AGENT_PORT);

        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage :");
            e.printStackTrace();
        }
    }
}
