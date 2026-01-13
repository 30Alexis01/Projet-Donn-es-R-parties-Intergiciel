package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientCSV {

    public static final Object lock = new Object();

    public static void main(String[] args) throws Exception {
        System.out.println("Agent scenario CSV");

        String server1Ip = "10.40.17.170";
        String server2Ip = "10.40.17.170";
        int serverPort1 = 2000;
        int serverPort2 = 2005;

        int myPort = 2001;
        Node myNode = new Node("10.40.17.29", myPort);

        // Serveur local pour le retour de l’agent
        try {
            new AgentServer(myNode.host, myNode.port).start();
        } catch (Exception e) {
            System.out.println(
                "Note : Serveur de retour déjà actif ou port occupé (" + e.getMessage() + ")"
            );
        }

        // JAR contenant StatsAgent
        byte[] code = JarUtils.loadJar("agents/test-agent.jar");

        int[] steps = {
            1,1,1,1,2,2,2,3,3,3,4,4,4,4,5,5,5,5,
            6,6,6,6,7,7,7,7,8,8,9,9,9,9,10,10,
            11,12,13,14,15,16,18,20,30,40,50,
            100,200,500,1000,2000,5000
        };

        for (int n : steps) {
            long start = System.nanoTime();

            StatsAgent agent = new StatsAgent();
            agent.init("Analyste-" + n, myNode);
            agent.setMaxLines(n);
            agent.setJarBytes(code);

            agent.addDestination(new Node(server2Ip, serverPort2));
            agent.move(new Node(server1Ip, serverPort1));

            synchronized (lock) {
                lock.wait();
            }

            long end = System.nanoTime();
            System.out.println(
                "Lignes csv : " + n + " ; Chrono : " + (end - start) / 1_000_000
            );

            Thread.sleep(300);
        }

        System.exit(0);
    }
}
