import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            System.out.println("=== SERVEUR RMI (Configuration en dur) ===");

            // ==========================================
            // 1. CONFIGURATION EN DUR
            // ==========================================
            
            // L'IP de la machine où tourne CE serveur.
            // Si c'est ton PC : "192.168.1.20"
            // Si c'est le PC de l'ami 1 : "192.168.1.50"
            String publicIp = "192.168.1.215"; 

            // Port RMI standard (doit correspondre au client)
            int port = 1099;

            // Chemin du fichier CSV (relatif au dossier d'exécution)
            // Si tu lances depuis la racine du projet, c'est peut-être "data/prenoms.csv"
            String csvPath = "data/prenoms.csv"; 

            // ==========================================
            // 2. DÉMARRAGE
            // ==========================================

            // IMPORTANT : Force RMI à utiliser cette IP pour les stubs
            // (Sinon il risque de donner 127.0.0.1 ou une IP locale injoignable par l'extérieur)
            System.setProperty("java.rmi.server.hostname", publicIp);

            // Création du registre RMI
            Registry registry = LocateRegistry.createRegistry(port);

            // Chargement du service
            NameService service = new NameServiceImpl(csvPath);

            // Enregistrement dans l'annuaire
            registry.rebind("NameService", service);

            System.out.println(">> Serveur prêt !");
            System.out.println(">> IP exposée : " + publicIp);
            System.out.println(">> Port       : " + port);
            System.out.println(">> Fichier CSV: " + csvPath);

        } catch (Exception e) {
            System.err.println("Erreur au démarrage du serveur :");
            e.printStackTrace();
        }
    }
}