package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientMain {
    
    public static final Object lock = new Object();

    public static void main(String[] args) throws Exception {
        System.out.println("=== BENCHMARK AGENT (2 SERVEURS) ===");

        // --- CONFIGURATION IP ---
        String myIp = "172.22.220.103";       // MOI (Client + Retour)
        String server1Ip = "147.127.133.195";  // PC AMI 1
        String server2Ip = "147.127.133.197";  // PC AMI 2 (ou réutiliser .182 si c'est le meme PC qui simule 2 srv)
        
        int portServer = 2000;
        int portClient = 2001;

        // Serveur de réception (pour le retour)
        Node myNode = new Node(myIp, portClient);
        new AgentServer(myNode.host, myNode.port).start();
        
        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

        int[] steps = {1,1,1,1, 10, 50, 100, 200, 500, 1000, 2000, 5000,10000, 50000, 100000,700000};
        System.out.println("MODE;REQUETES_PAR_SRV;TEMPS_NS");

        for (int n : steps) {
            long start = System.nanoTime();

            StatsAgent agent = new StatsAgent();
            agent.init("Voyageur-" + n, myNode);
            agent.setMaxLines(n);
            agent.setJarBytes(code);
            
            // --- ITINÉRAIRE (La boucle) ---
            // 1. Aller vers Serveur 1
            // 2. Aller vers Serveur 2 (depuis le 1)
            // 3. Retour vers Moi (depuis le 2)
            
            agent.addDestination(new Node(server2Ip, portServer)); // Sera visité après le 1er saut
            // Départ vers la PREMIÈRE destination
            agent.move(new Node(server1Ip, portServer));

            // Attente
            synchronized (lock) {
                lock.wait();
            }

            long end = System.nanoTime();
            System.out.println("AGENT;" + n + ";" + (end - start)/1000000+"ms");
            
            Thread.sleep(500);
        }
        System.exit(0);
    }
}