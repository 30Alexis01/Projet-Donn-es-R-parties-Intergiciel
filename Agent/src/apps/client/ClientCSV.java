package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientCSV {
    
    public static final Object lock = new Object();

    // Configuration
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2000;

    public static void main(String[] args) throws Exception {
        System.out.println("=== BENCHMARK AGENT (SCENARIO CSV) ===");

        // 1. Serveur de retour (Attention : port différent du ClientZip pour éviter les conflits si lancés ensemble)
        // Mais si tu lances l'un OU l'autre, 2001 suffit.
        int myPort = 2001; 
        Node myNode = new Node("127.0.0.1", myPort);
        
        // On vérifie si un serveur tourne déjà sur ce port (au cas où ClientMain serait actif)
        // Pour faire simple ici, on tente de le lancer.
        try {
            new AgentServer(myNode.host, myNode.port).start();
        } catch (Exception e) {
            System.out.println("Note : Serveur de retour déjà actif ou port occupé (" + e.getMessage() + ")");
        }
        
        // 2. Chargement du JAR
        // Ce JAR devra contenir StatsAgent.class !
        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

        // Paliers de test
        int[] steps = {10, 100, 1000, 10000}; 
        System.out.println("LIGNES_CSV;TEMPS_TOTAL_MS");

        for (int n : steps) {
            long start = System.nanoTime();

            // 3. Initialisation de l'agent CSV (StatsAgent)
            StatsAgent agent = new StatsAgent();
            agent.init("Analyste-" + n, myNode);
            // On suppose que ton StatsAgent a cette méthode (sinon adapte selon ton code original)
            // Si StatsAgent n'existe plus, il faudra le recréer !
            // agent.setMaxLines(n); 
            
            agent.setJarBytes(code);
            
            // 4. Envoi
            agent.move(new Node(SERVER_IP, SERVER_PORT));

            // 5. Attente
            synchronized (lock) {
                lock.wait();
            }

            long end = System.nanoTime();
            System.out.println(n + ";" + (end - start)/1_000_000);
            
            Thread.sleep(500);
        }
        System.exit(0);
    }
}