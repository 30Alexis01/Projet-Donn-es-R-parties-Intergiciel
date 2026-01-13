import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            String ipServeur = "127.0.0.1"; 
            int port = 2003;
            
            String csvPath = "data/prenoms.csv";     
            String filesPath = "server_files";      

            // Configuration RMI pour que le client puisse nous contacter
            System.setProperty("java.rmi.server.hostname", ipServeur);

            // 1. Démarrage de l'annuaire RMI (Registry)
            Registry registry = LocateRegistry.createRegistry(port);
            System.out.println("Registre RMI démarré sur le port " + port);

            // 2. Service 1 : CSV (Existant)
            NameService nameService = new NameServiceImpl(csvPath);
            registry.rebind("NameService", nameService);
            System.out.println("Service 'NameService' (CSV) enregistré.");

            // 3. Service 2 : Fichiers (Nouveau)
            RemoteFileService fileService = new RemoteFileServiceImpl(filesPath);
            registry.rebind("FileServiceRMI", fileService);
            System.out.println("Service 'FileServiceRMI' (Fichiers) enregistré.");

            System.out.println(">> Serveur prêt !");

        } catch (Exception e) {
            System.err.println("Erreur serveur :");
            e.printStackTrace();
        }
    }
}