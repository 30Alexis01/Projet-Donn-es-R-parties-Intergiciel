package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CLIENT AGENT (Performance Test) ===");

        // 1. TON IP (Pour le retour)
        // Mets ici ton IP locale (ex: 192.168.1.20)
        String myIp = "192.168.1.215"; 
        Node myNode = new Node(myIp, 2000);
        
        AgentServer clientListener = new AgentServer(myNode.host, myNode.port);
        clientListener.start();

        // 2. L'IP DE TON AMI (Serveur)
        // Mets ici l'IP de ton ami (ex: 192.168.1.50)
        String serverIp = "192.168.1.182";
        Node serverNode = new Node(serverIp, 2001);

        // 3. Création de l'agent
        StatsAgent agent = new StatsAgent();
        agent.init("AgentSmith", myNode);
        
        // --- CORRECTION ICI ---
        // On ne cherche plus "MARIE", on veut lire 10 000 lignes
        agent.setMaxLines(10000); 
        // ----------------------

        // 4. Chargement du code
        byte[] jarBytes = JarUtils.loadJar("agents/test-agent.jar");
        agent.setJarBytes(jarBytes);

        System.out.println("Envoi de l'agent vers " + serverIp + "...");
        agent.move(serverNode);
    }
}
//compiler : 
//javac -d bin -sourcepath src src/apps/server/ServerMain.java src/apps/client/*.java src/agents/*.java src/platform/common/*.java src/platform/agent/*.java src/platform/transport/*.java src/platform/server/*.java src/platform/service/*.java
//lancer : 
//java -cp bin apps.server.ServerMain 2001