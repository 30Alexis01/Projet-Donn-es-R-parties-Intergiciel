package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientCSV {
    
    public static final Object lock = new Object();

    // Configuration
    private static final String SERVER_IP = "10.40.17.170";
    private static final int SERVER_PORT = 2000;      // Premier serveur
    private static final int SERVER_PORT_2 = 2005;    // Second serveur

    public static void main(String[] args) throws Exception {
        System.out.println("Agent version CSV");

        int myPort = 2001; 
        Node myNode = new Node("10.40.17.29", myPort);
        
        // On vérifie si un serveur tourne déjà sur ce port (au cas où ClientMain serait actif)
        try {
            new AgentServer(myNode.host, myNode.port).start();
        } catch (Exception e) {
            System.out.println("Note : Serveur de retour déjà actif ou port occupé (" + e.getMessage() + ")");
        }
        
        // Ce JAR devra contenir StatsAgent.class
        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

        // Paliers de test
        int[] steps = {1, 1, 1, 1, 2,2,2,3,3,3,4,4,4,4,5,5,5,5,6,6,6,6,7,7,7,7,8,8,9,9,9,9,10,10,11,12,13,14,15,16,18,20,30,40, 50, 100, 200, 500, 1000, 2000, 5000};
        
        for (int n : steps) {
            long start = System.nanoTime();

            // Initialisation de l'agent CSV (StatsAgent)
            StatsAgent agent = new StatsAgent();
            agent.init("Analyste-" + n, myNode);
            agent.setMaxLines(n); 
            
            agent.setJarBytes(code); // on donne son code à l'agent
          
            agent.addDestination(new Node(SERVER_IP, SERVER_PORT_2));
            
            agent.move(new Node(SERVER_IP, SERVER_PORT));

      
            // Attente
            synchronized (lock) {
                lock.wait();
            }

            long end = System.nanoTime();
            System.out.println("Lignes csv : "+n + "; Chrono : " + (end - start)/1000000);
            
            Thread.sleep(300);
        }
        System.exit(0);
    }
}