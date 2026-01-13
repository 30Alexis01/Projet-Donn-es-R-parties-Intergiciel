import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ClientRMIFiles {

    public static void main(String[] args) {
        System.out.println(" RMI SCENARIO FICHIERS");

        String server1Ip = "10.40.17.170";
        int rmiPort1 = 2003;

        // Dossiers de réception
        String destDir1 = "client_results_rmi_srv1";

        // Même steps que ton ClientMain
          int[] steps = {
    1,1,1,1,1,1,1,1,1,1,
    2,2,2,
    3,3,3,
    4,4,4,
    5,5,5,
    6,6,6,
    7,7,7,
    8,8,8,
    9,9,9,
    10,10,10,
    11,11,11,
    12,12,12,
    13,13,13,
    14,14,14,
    15,15,15,
    16,16,16,
    17,17,17,
    18,18,18,
    19,19,19,
    20,20,20,
    21,21,21,
    22,22,22,
    23,23,23,
    24,24,24,
    25,25,25,
    26,26,26,
    27,27,27,
    28,28,28,
    29,29,29,
    30,30,30,
    31,31,31,
    32,32,32,
    33,33,33,
    34,34,34,
    35,35,35,
    36,36,36,
    37,37,37,
    38,38,38,
    39,39,39,
    40,40,40,
    41,41,41,
    42,42,42,
    43,43,43,
    44,44,44,
    45,45,45,
    46,46,46,
    47,47,47,
    48,48,48,
    49,49,49,
    50,50,50,
    51,51,51,
    52,52,52,
    53,53,53,
    54,54,54,
    55,55,55,
    56,56,56,
    57,57,57,
    58,58,58,
    59,59,59,
    60,60,60,
    61,61,61,
    62,62,62,
    63,63,63,
    64,64,64,
    65,65,65,
    66,66,66,
    67,67,67,
    68,68,68,
    69,69,69,
    70,70,70
};

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

                System.out.println("Nombre de fichiers : " + n + " -> temps : " + (end - start) / 1000000 + "ms");

            } catch (Exception e) {
                System.err.println("Erreur RMI (" + n + ") : " + e.getMessage());
            }
        }
    }
}
