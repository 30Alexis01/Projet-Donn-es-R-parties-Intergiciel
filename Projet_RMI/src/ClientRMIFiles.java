import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ClientRMIFiles {

    public static void main(String[] args) {
        System.out.println(" RMI SCENARIO FICHIERS");

        String server1Ip = "172.22.223.116";
        int rmiPort1 = 2003;

        // Dossiers de réception
        String destDir1 = "client_results_rmi_srv1";

        // Même steps que ton ClientMain
        int[] steps = {1, 1, 1, 1, 10, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 50000, 100000};

        for (int n : steps) {
            try {
                // Préparation des dossiers
                File d1 = new File(destDir1);
                if (!d1.exists()) d1.mkdirs();


                // Connexion registre + récupération des stubs (comme ton ClientMain)
                Registry reg1 = LocateRegistry.getRegistry(server1Ip, rmiPort1);
                RemoteFileService service1 = (RemoteFileService) reg1.lookup("FileServiceRMI");

                service1.printServer("Je suis dans le serveur" + server1Ip +"au step " + n);


                // Préparation des noms de fichiers à récupérer 
                List<String> filesToFetch = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    filesToFetch.add("doc_" + i + ".txt");
                }

                long start = System.nanoTime();

                for (String fileName : filesToFetch) {
                    try {
                        byte[] data = service1.downloadFile(fileName);

                        File target = new File(d1, fileName);
                        FileOutputStream fos = new FileOutputStream(target);
                        fos.write(data);
                        fos.close();
                    } catch (Exception e) {
                        System.err.println("Erreur srv1 sur " + fileName + " : " + e.getMessage());
                    }
                }

                
                long end = System.nanoTime();

                System.out.println("Nombre de fichiers : " + n + " -> temps : " + (end - start) / 1_000_000);

            } catch (Exception e) {
                System.err.println("Erreur RMI (" + n + ") : " + e.getMessage());
            }
        }
    }
}
