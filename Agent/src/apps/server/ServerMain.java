package apps.server;

import platform.server.AgentServer;
import platform.service.NameService;

/**
 * Point d'entrée du serveur "Agents".
 * Lance uniquement :
 * - un AgentServer
 * - avec le service NameService (CsvNameService)
 * - avec le service FileService (LocalFileService) POUR LE SCÉNARIO ZIP
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

    // Chemin du dossier contenant les fichiers à télécharger (Step 1)
    private static final String FILES_PATH = "server_files";

    // Noms utilisés dans le NameServer (l'annuaire)
    private static final String SERVICE_NAME_CSV = "NameService";
    private static final String SERVICE_NAME_FILE = "FileService";

    // =========================

    public static void main(String[] args) {
        try {
            System.out.println("=== Démarrage du serveur Agents ===");
            System.out.println("Listen IP : " + LISTEN_IP);
            System.out.println("Port      : " + AGENT_PORT);
            System.out.println("CSV       : " + CSV_PATH);
            System.out.println("Files Dir : " + FILES_PATH);

            // 1) Charger les services locaux
            // Service existant (CSV)
            NameService csvService = new CsvNameService(CSV_PATH);
            
            // NOUVEAU : Service Fichiers (Lecture disque)
            LocalFileService fileService = new LocalFileService(FILES_PATH);

            // 2) Démarrer le serveur d'agents
            AgentServer server = new AgentServer(LISTEN_IP, AGENT_PORT);

            // 3) Injecter les services dans l'annuaire
            // L'agent pourra les récupérer via agent.getNameServer().get("Clé")
            server.getNameServer().put(SERVICE_NAME_CSV, csvService);
            server.getNameServer().put(SERVICE_NAME_FILE, fileService);

            // 4) Lancer
            server.start();
            System.out.println(">> [Agent] Serveur prêt sur " + LISTEN_IP + ":" + AGENT_PORT);
            System.out.println(">> Services disponibles : " + server.getNameServer().keySet());

        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage :");
            e.printStackTrace();
        }
    }
}