import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ServerMain <port> <csvPath>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String csvPath = args[1];

        // 1) Démarrer le registre RMI sur ce port
        Registry registry = LocateRegistry.createRegistry(port);

        // 2) Créer l’objet distant
        NameService service = new NameServiceImpl(csvPath);

        // 3) Publier (bind) dans le registre avec un nom
        registry.rebind("NameService", service);

        System.out.println("Server ready on port " + port);
        System.out.println("Bound name: NameService");
    }
}
