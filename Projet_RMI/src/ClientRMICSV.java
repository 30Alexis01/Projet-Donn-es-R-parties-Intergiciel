import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMICSV {
    public static void main(String[] args) {
        System.out.println(" RMI SCENARIO CSV");

        String server1Ip = "172.22.223.116"; 
        String server2Ip = "172.22.223.116"; 
        int rmiPort1 = 2003;
        int rmiPort2 = 2004;
        
        // Répétition de n = 1 pour ignorer l'effet warm up 
        // Le premier appel est plus lent à cause des initialisations (connexion, JVM, cache)
        int[] steps = {1, 1, 1, 1, 10, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 50000, 100000};
        

        for (int n : steps) {
            
            
            try {
                long total = 0;
                Registry reg1 = LocateRegistry.getRegistry(server1Ip, rmiPort1);
                CSVService service1 = (CSVService) reg1.lookup("NameService");

                service1.printServer("Je suis dans le serveur" + server1Ip +"au step " + n);

                Registry reg2 = LocateRegistry.getRegistry(server2Ip, rmiPort2);
                CSVService service2 = (CSVService) reg2.lookup("NameService");

                service2.printServer("Je suis dans le serveur" + server2Ip + "au step " + n );


                long start = System.nanoTime(); 
                
                for (int i = 0; i < n; i++) {
                    total += service1.getCountByLine(i);
                }

                
                for (int i = 0; i < n; i++) {
                    total += service2.getCountByLine(i);
                }

                long end = System.nanoTime(); 
               

                System.out.println("Nombre de requête : " + n + "-> temps : " + (end - start)/1000000);
                
            } catch (Exception e) {
                System.err.println("Erreur RMI (" + n + ") : " + e.getMessage());
            }
        }
    }
}