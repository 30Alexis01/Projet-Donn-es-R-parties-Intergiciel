package apps.client;

import agents.StatsAgent;
import platform.common.Node;
import platform.server.AgentServer;
import platform.transport.JarUtils;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        System.out.println("=== CLIENT AGENT (MULTI-SAUTS : BOUCLE) ===");

        // ==========================================
        // 1. CONFIGURATION DES IP
        // ==========================================
        
        // TON IP (Celle de ta machine, où tournent ce Client ET ton Serveur)
        String myIp = "192.168.1.215";       // <--- METTRE TON IP ICI
        
        // IP DE TON AMI (Le premier saut)
        String friendIp = "192.168.1.182";   // <--- METTRE IP DE L'AMI
        
        // ==========================================
        // 2. CONFIGURATION DES PORTS
        // ==========================================
        
        // Port utilisé par les SERVEURS (Ton ServerMain et celui de ton ami)
        int portServer = 2000;
        
        // Port utilisé par CE CLIENT (Pour ne pas bloquer le port 2000 sur ta machine)
        int portClient = 2001; 

        // ==========================================
        // 3. DÉMARRAGE DE LA RÉCEPTION (CLIENT)
        // ==========================================
        // On écoute sur le port 2001 pour recevoir l'agent à la toute fin.
        Node myNode = new Node(myIp, portClient);
        new AgentServer(myNode.host, myNode.port).start();
        System.out.println(">> Client prêt à recevoir le retour sur " + myIp + ":" + portClient);

        // ==========================================
        // 4. PRÉPARATION DE L'AGENT
        // ==========================================
        StatsAgent agent = new StatsAgent();
        agent.init("Voyageur", myNode); // Il se souvient qu'il vient de 'myNode' (2001)
        agent.setMaxLines(5000); 

        // ==========================================
        // 5. CRÉATION DE L'ITINÉRAIRE
        // ==========================================
        // Rappel du trajet : MOI(Client) -> AMI -> MOI(Serveur) -> MOI(Client/Retour)
        
        // L'agent va partir physiquement vers l'AMI (voir étape 7).
        // On empile ici les destinations pour la SUITE du voyage.
        
        // Après l'ami, il doit aller sur TON SERVEUR (Port 2000)
        agent.addDestination(new Node(myIp, portServer));
        
        // Après ton serveur, il doit revenir sur TON CLIENT (Port 2001) pour afficher les résultats
        agent.addDestination(new Node(myIp, portClient));

        // ==========================================
        // 6. CHARGEMENT DU CODE
        // ==========================================
        agent.setJarBytes(JarUtils.loadJar("agents/test-agent.jar"));

        // ==========================================
        // 7. DÉPART
        // ==========================================
        System.out.println(">> Lancement de l'agent vers l'Ami : " + friendIp + ":" + portServer);
        
        // Premier saut : On l'envoie chez l'ami (sur son port serveur 2000)
        agent.move(new Node(friendIp, portServer));
    }
}