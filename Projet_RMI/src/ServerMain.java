import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            String ipServeur = "localhost"; 
            int port = Integer.parseInt(args[0]);
            
            String csvPath = "data/prenoms.csv";     
            String filesPath = "server_files";      

            System.setProperty("java.rmi.server.hostname", ipServeur);

            Registry registry = LocateRegistry.createRegistry(port);
            System.out.println("Registre RMI démarré sur le port " + port);

            CSVService nameService = new CSVServiceImpl(csvPath);
            registry.rebind("NameService", nameService);
            System.out.println("Service 'NameService' (CSV) enregistré.");

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