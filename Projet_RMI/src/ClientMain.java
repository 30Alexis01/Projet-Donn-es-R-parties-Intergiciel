import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println(
                "Usage: java ClientMain <server_ip> <port> [n]\n" +
                "  <server_ip> : adresse IP ou hostname du serveur RMI\n" +
                "  <port>      : port du registre RMI\n" +
                "  [n]         : nombre de lignes à sommer (optionnel, défaut = toutes)"
            );
            return;
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        // Nombre de lignes à sommer
        int n = -1; // -1 = toutes les lignes
        if (args.length >= 3) {
            n = Integer.parseInt(args[2]);
        }

        // 1) Connexion au registre RMI distant
        Registry registry = LocateRegistry.getRegistry(serverIp, port);

        // 2) Lookup du service
        NameService service = (NameService) registry.lookup("NameService");

        // Nombre total de lignes disponibles
        int max = service.getNbLines();
        if (n < 0 || n > max) {
            n = max;
        }

        long sum = 0;

        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            sum += service.getCountByLine(i); // 1 appel RMI par ligne
        }
        long t1 = System.nanoTime();

        System.out.println("Somme des " + n + " premières lignes = " + sum);
        System.out.println("Time (ms) = " + ((t1 - t0) / 1_000_000));
    }
}
