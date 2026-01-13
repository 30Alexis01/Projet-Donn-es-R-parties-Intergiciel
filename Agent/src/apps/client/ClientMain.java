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

public class ClientMain {
    
    // Le verrou partagé avec l'Agent pour la synchronisation (wait/notify)
    public static final Object lock = new Object();
    
   
    // L'agent clone qui revient déposera ses données ici.
    public static byte[] receivedData = null; 


    private static final String SERVER_IP = "127.0.0.1"; 
    private static final int SERVER_PORT = 2000;
    
    // Dossier où on va écrire les fichiers reçus
    private static final String DEST_DIR = "client_results";

    public static void main(String[] args) throws Exception {
        System.out.println("=== BENCHMARK AGENT (SCENARIO ZIP) ===");

        
        int myPort = 2001;
        Node myNode = new Node("127.0.0.1", myPort);
        new AgentServer(myNode.host, myNode.port).start();//on créé notre propre serveur pour récupérer l'agent

        
        File destDir = new File(DEST_DIR);
        if (!destDir.exists()) destDir.mkdirs();

        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

        int[] steps = {1,1,1,1,2,3,4,5,6,7,8,9 ,10, 15,20, 30, 40, 50, 100}; 

        System.out.println("NB_FICHIERS;TEMPS_TOTAL_MS");

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
            
            System.out.println("nbFiles : "+nbFiles + "; Chrono :" +" "+ (end - start) / 1000000);
            
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