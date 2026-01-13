import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {

            String IpServeur = "172.22.220.103"; 
            int port = Integer.parseInt(args[0]);
            String csvPath = "data/prenoms.csv"; 


            // Confirme l'Ip aux serveurs distants
            //System.setProperty("java.rmi.server.hostname", IpServeur);

            // Création du registre RMI
            Registry registry = LocateRegistry.createRegistry(port);

            // Chargement du service
            NameService service = new NameServiceImpl(csvPath);

            // Enregistrement dans l'annuaire
            registry.rebind("NameService", service);

            System.out.println("Serveur prêt !");
            System.out.println("IP du Serveur : " + IpServeur);
            System.out.println("Port : " + port);

        } catch (Exception e) {
            System.err.println("Erreur au démarrage du serveur :");
            e.printStackTrace();
        }
    }
}