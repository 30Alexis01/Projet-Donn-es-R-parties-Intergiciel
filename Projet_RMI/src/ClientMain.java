import platform.service.NameService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("=== CLIENT RMI (MULTI-SERVEURS) ===");

        // --- CONFIGURATION EN DUR ---
        String server1Ip = "192.168.1.50"; // <--- METTRE IP PC 1
        String server2Ip = "192.168.1.60"; // <--- METTRE IP PC 2
        
        int rmiPort = 1099; // Port standard
        int linesPerServer = 5000;

        long total = 0;
        long start = System.currentTimeMillis();

        try {
            // --- ÉTAPE 1 : Appel Serveur 1 ---
            System.out.println("Connexion au Serveur 1 (" + server1Ip + ")...");
            Registry reg1 = LocateRegistry.getRegistry(server1Ip, rmiPort);
            NameService service1 = (NameService) reg1.lookup("NameService");
            
            for (int i = 0; i < linesPerServer; i++) {
                total += service1.getCountByLine(i);
            }
            System.out.println("Serveur 1 terminé.");

            // --- ÉTAPE 2 : Appel Serveur 2 ---
            System.out.println("Connexion au Serveur 2 (" + server2Ip + ")...");
            Registry reg2 = LocateRegistry.getRegistry(server2Ip, rmiPort);
            NameService service2 = (NameService) reg2.lookup("NameService");
            
            for (int i = 0; i < linesPerServer; i++) {
                total += service2.getCountByLine(i);
            }
            System.out.println("Serveur 2 terminé.");

        } catch (Exception e) {
            System.err.println("Erreur RMI : " + e.getMessage());
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("----------------------------------");
        System.out.println("TOTAL FINAL RMI : " + total);
        System.out.println("TEMPS TOTAL     : " + (end - start) + " ms");
        System.out.println("----------------------------------");
    }
}