import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("=== BENCHMARK RMI (2 SERVEURS) - LOOKUP EXCLU ===");

        // --- CONFIGURATION IP ---
        String server1Ip = "147.127.135.141"; 
        String server2Ip = "147.127.135.142"; 
        int rmiPort = 2003;
        
        // Paliers de test
        int[] steps = {1, 1, 1, 1, 10, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 50000, 100000};

        System.out.println("MODE;REQUETES_PAR_SRV;TEMPS_NS");

        for (int n : steps) {
            long total = 0;
            
            try {
                // ==========================================
                // 1. PRÉPARATION (HORS CHRONO)
                // ==========================================
                // On établit toutes les connexions AVANT de déclencher le chronomètre.
                // Ainsi, on ne mesure que la "communication", pas la "découverte".
                
                Registry reg1 = LocateRegistry.getRegistry(server1Ip, rmiPort);
                NameService service1 = (NameService) reg1.lookup("NameService");

                Registry reg2 = LocateRegistry.getRegistry(server2Ip, rmiPort);
                NameService service2 = (NameService) reg2.lookup("NameService");

                // ==========================================
                // 2. MESURE DE PERFORMANCE (BOUCLE PURE)
                // ==========================================
                long start = System.nanoTime(); 
                
                // Boucle Serveur 1
                for (int i = 0; i < n; i++) {
                    total += service1.getCountByLine(i);
                }

                // Boucle Serveur 2
                for (int i = 0; i < n; i++) {
                    total += service2.getCountByLine(i);
                }

                long end = System.nanoTime(); 
                // ==========================================

                System.out.println("RMI;" + n + ";" + (end - start));
                
            } catch (Exception e) {
                System.err.println("Erreur RMI (" + n + ") : " + e.getMessage());
            }
        }
    }
}