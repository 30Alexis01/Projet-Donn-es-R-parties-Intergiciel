import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ClientMain <host> <port> [n]");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // nombre de lignes à sommer (par défaut 10000)
        int n=0;
        if (args.length >= 3) {
            n = Integer.parseInt(args[2]);
        }

        // 1) récupérer le registry
        Registry registry = LocateRegistry.getRegistry(host, port);

        // 2) lookup -> stub
        NameService service = (NameService) registry.lookup("NameService");

        // Option : si tu veux éviter de dépasser, tu peux borner avec le nb réel
        int max = service.getNbLines();
        if (n > max) n = max;

        long sum = 0;

        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            sum += service.getCountByLine(i); // 1 appel RMI par ligne
        }
        long t1 = System.nanoTime();

        System.out.println("Somme des " + n + " premières lignes = " + sum);
        // si tu veux aussi en ms (optionnel)
        System.out.println("Time (ms) = " + ((t1 - t0) / 1_000_000));
    }
}
