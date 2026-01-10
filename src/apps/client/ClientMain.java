package apps.client;


import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;
// ... imports
import agents.StatsAgent;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        // ... (Code de démarrage du serveur écouteur sur port 2000 identique à avant) ...
        String myIp = "192.168.1.215";
        Node myNode = new Node(myIp, 2000);
        AgentServer clientListener = new AgentServer(myNode.host, myNode.port);
        clientListener.start();
        String serverIp ="192.168.1.182";
        Node serverNode = new Node(serverIp, 2001); // Ou l'IP de ton ami

        // CRÉATION DE L'AGENT STATISTIQUE
        StatsAgent agent = new StatsAgent();
        agent.init("AgentSmith", myNode);
        
        // ON LUI DONNE SA MISSION
        agent.setMission("MARIE", 1900, 2022);

        // Chargement du code
        byte[] jarBytes = JarUtils.loadJar("agents/test-agent.jar");
        agent.setJarBytes(jarBytes);

        System.out.println("Envoi de l'agent...");
        agent.move(serverNode);
    }
}