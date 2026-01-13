package apps.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import agents.ZipAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientFile {
    
    // Le verrou partagé avec l'Agent pour la synchronisation (wait/notify)
    public static final Object lock = new Object();
    
   
    // L'agent clone qui revient déposera ses données ici.
    public static byte[] receivedData = null; 


    private static final String SERVER_IP = "localhost"; 
    private static final int SERVER_PORT = 2000;
    
    // Dossier où on va écrire les fichiers reçus
    private static final String DEST_DIR = "client_results";

    public static void main(String[] args) throws Exception {
        System.out.println("Agent version Files");

        
        int myPort = 2001;
        Node myNode = new Node("10.40.17.29", myPort);
        new AgentServer(myNode.host, myNode.port).start();//on créé notre propre serveur pour récupérer l'agent

        
        File destDir = new File(DEST_DIR);
        if (!destDir.exists()) destDir.mkdirs();

        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

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

       

        for (int nbFiles : steps) {
            // Préparation de la liste des fichiers à demander
            List<String> filesToFetch = new ArrayList<>();
            for (int i = 0; i < nbFiles; i++) {
                filesToFetch.add("doc_" + i + ".txt");
            }

            //on vide avant le prochain envoi
            receivedData = null;

            long start = System.nanoTime();

            // Initialisation HAgentmon
            ZipAgent agent = new ZipAgent(filesToFetch);
            agent.init("HAgent-Mhagicmon" + nbFiles, myNode);//on lui donne l'adresse du noued de départ pour qu'il puisse rentrer
            agent.setJarBytes(code);

            // Envoi
            agent.move(new Node(SERVER_IP, SERVER_PORT));

            //Attente du retour
            synchronized (lock) {
                lock.wait();
            }

            // Récupération & Décompression
            byte[] zipData = receivedData; 

            if (zipData != null) {
                unzip(zipData, destDir);
            } else {
                System.err.println("ERREUR : Pas de données reçues pour " + nbFiles + " fichiers !");
            }

           //on a dézippé, on arrête le temps
            long end = System.nanoTime();
            
            System.out.println("nbFiles : "+nbFiles + "; Chrono :" +" "+ (end - start) / 1000000+ "ms");
            
            Thread.sleep(200);
        }
        
        System.out.println("Fin du benchmark.");
        System.exit(0);
    }

    /**
     * Utilitaire pour dézipper les données reçues sur le disque.
     */
    private static void unzip(byte[] data, File destDir) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File targetFile = new File(destDir, entry.getName());
                
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}