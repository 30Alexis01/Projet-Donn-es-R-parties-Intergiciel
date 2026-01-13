package apps.server;

import platform.server.AgentServer;
import platform.service.NameService;


public class ServerMain {

    private static final String LISTEN_IP = "localhost";
    private static final int AGENT_PORT = 2000;
    private static final String CSV_PATH = "prenoms.csv";
    private static final String FILES_PATH = "server_files";

    // Noms utilisés dans le NameServer (l'annuaire)
    private static final String SERVICE_NAME_CSV = "NameService";
    private static final String SERVICE_NAME_FILE = "FileService";


    public static void main(String[] args) {
        try {
            System.out.println("Démarrage du serveur Agents ");
            System.out.println("Listen IP : " + LISTEN_IP);
            System.out.println("Port      : " + Integer.parseInt(args[0]));
        

            // Service existant (CSV)
            NameService csvService = new CsvNameService(CSV_PATH);
            
            LocalFileService fileService = new LocalFileService(FILES_PATH);

            AgentServer server = new AgentServer(LISTEN_IP, Integer.parseInt(args[0]));

            // L'agent pourra les récupérer via agent.getNameServer().get("Clé")
            server.getNameServer().put(SERVICE_NAME_CSV, csvService);
            server.getNameServer().put(SERVICE_NAME_FILE, fileService);

            // 4) Lancer
            server.start();
            System.out.println("[Agent] Serveur prêt sur " + LISTEN_IP + ":" + AGENT_PORT);
            System.out.println(">> Services disponibles : " + server.getNameServer().keySet());

        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage :");
            e.printStackTrace();
        }
    }
}