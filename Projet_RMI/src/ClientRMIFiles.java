import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ClientRMIFiles {

    // === CONFIGURATION CLIENT ===
    private static final String SERVER_IP = "127.0.0.1"; // Doit correspondre à ServerMain
    private static final int SERVER_PORT = 1099;
    
    // Dossier de réception (pour être équitable avec l'Agent qui écrit sur disque)
    private static final String DEST_DIR = "client_results_rmi";

    public static void main(String[] args) {
        try {
            System.out.println("=== BENCHMARK RMI (SCENARIO FICHIERS) ===");

            // 1. Préparation du dossier de réception
            File destDir = new File(DEST_DIR);
            if (!destDir.exists()) destDir.mkdirs();

            // 2. Connexion au registre
            Registry registry = LocateRegistry.getRegistry(SERVER_IP, SERVER_PORT);
            
            // 3. Récupération du stub du service fichier
            RemoteFileService service = (RemoteFileService) registry.lookup("FileServiceRMI");

            // 4. Benchmark
            int[] steps = {1, 10, 50, 100};
            System.out.println("NB_FICHIERS;TEMPS_TOTAL_MS");

            for (int nbFiles : steps) {
                
                // Préparation de la liste des noms (doc_0.txt, doc_1.txt...)
                List<String> filesToFetch = new ArrayList<>();
                for (int i = 0; i < nbFiles; i++) {
                    filesToFetch.add("doc_" + i + ".txt");
                }

                long start = System.nanoTime();

                // --- BOUCLE RMI ---
                // On doit faire 1 appel réseau par fichier
                for (String fileName : filesToFetch) {
                    try {
                        // A. Téléchargement (Réseau)
                        byte[] data = service.downloadFile(fileName);
                        
                        // B. Écriture (Disque)
                        File target = new File(destDir, fileName);
                        try (FileOutputStream fos = new FileOutputStream(target)) {
                            fos.write(data);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur sur " + fileName + ": " + e.getMessage());
                    }
                }
                // ------------------

                long end = System.nanoTime();
                System.out.println(nbFiles + ";" + (end - start) / 1_000_000);
                
                // Pause pour laisser le système respirer
                Thread.sleep(500);
            }

            System.out.println("Fin du benchmark RMI.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}