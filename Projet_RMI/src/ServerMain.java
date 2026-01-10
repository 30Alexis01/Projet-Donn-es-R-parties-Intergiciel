import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java ServerMain <publicIp> <port> <csvPath> ");
            return;
        }


        String publicIp = args[0];
        int port = Integer.parseInt(args[1]);
        String csvPath = args[2];
        

        // IMPORTANT : l'IP que les stubs vont annoncer aux clients
        System.setProperty("java.rmi.server.hostname", publicIp);

        Registry registry = LocateRegistry.createRegistry(port);
        NameService service = new NameServiceImpl(csvPath);
        registry.rebind("NameService", service);

        System.out.println("Server ready on " + publicIp + ":" + port);
        System.out.println("Bound name: NameService");
    }
}
