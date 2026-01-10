package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("=== CLIENT AGENT (MULTI-SAUTS) ===");

        // --- CONFIGURATION EN DUR ---
        // 1. MON IP (Client)
        String myIp = "192.168.1.20"; // <--- METTRE TON IP ICI
        
        // 2. IP DU PREMIER SERVEUR (Le PC de ton Ami 1)
        String server1Ip = "192.168.1.50"; // <--- METTRE IP PC 1
        
        // 3. IP DU DEUXIÈME SERVEUR (Le PC de ton Ami 2 ou un autre)
        String server2Ip = "192.168.1.60"; // <--- METTRE IP PC 2
        
        // Ports (Généralement 2001 partout pour simplifier)
        int portAgent = 2001; 

        // --- Démarrage Réception ---
        Node myNode = new Node(myIp, 2000);
        new AgentServer(myNode.host, myNode.port).start();

        // --- Préparation de l'Agent ---
        StatsAgent agent = new StatsAgent();
        agent.init("Voyageur", myNode);
        agent.setMaxLines(5000); // Il lira 5000 lignes sur CHAQUE serveur

        // --- CRÉATION DE L'ITINÉRAIRE ---
        // L'agent va aller physiquement sur server1.
        // On lui dit : "Quand tu as fini sur server1, va sur server2".
        agent.addDestination(new Node(server2Ip, portAgent));

        // Chargement du code
        agent.setJarBytes(JarUtils.loadJar("agents/test-agent.jar"));

        // --- DÉPART ---
        System.out.println("Envoi de l'agent vers le 1er serveur : " + server1Ip);
        // On l'envoie manuellement au premier point. Ensuite il se débrouille.
        agent.move(new Node(server1Ip, portAgent));
    }
}