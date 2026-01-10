package apps.server;

import platform.server.AgentServer;

/**
 * Point d'entrée du Serveur d'Agents.
 * Lance la plateforme et enregistre les services locaux (ex: Base de données Prénoms).
 */
public class ServerMain {

    public static void main(String[] args) {
        // 1. Vérification des arguments (Port)
        if (args.length < 1) {
            System.out.println("Usage: java apps.server.ServerMain <port>");
            return;
        }
        
        int port = Integer.parseInt(args[0]);

        try {
            System.out.println("=== Démarrage du Serveur d'Agents sur le port " + port + " ===");

            // 2. Création du serveur
            AgentServer server = new AgentServer("localhost", port);
            
            // 3. ENREGISTREMENT DES SERVICES (C'est ici qu'on donne l'intelligence locale)
            // On associe la clé "NameService" à notre implémentation qui lit le CSV.
            // Assure-toi que "prenoms.csv" est bien à la racine du projet (là où tu lances java).
            String csvPath = "prenoms.csv";
            server.getNameServer().put("NameService", new CsvNameService(csvPath));
            
            System.out.println("Service 'NameService' enregistré avec succès.");

            // 4. Lancement du serveur (Bloquant dans un thread à part, mais main reste vivant)
            server.start();
            
        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage du serveur :");
            e.printStackTrace();
        }
    }
}