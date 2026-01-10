package apps.server;

import platform.server.AgentServer;
import platform.service.NameService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Point d'entrée du Serveur.
 * Lance :
 * 1. Le serveur d'agents (AgentServer) pour l'approche mobile.
 * 2. Un serveur TCP simple (RPC) pour l'approche classique/naïve.
 */
public class ServerMain {

    public static void main(String[] args) {
        // 1. Vérification des arguments
        if (args.length < 1) {
            System.out.println("Usage: java apps.server.ServerMain <port>");
            return;
        }
        
        int port = Integer.parseInt(args[0]); // Port pour les Agents (ex: 2001)
        int rpcPort = port + 1;               // Port pour le RPC Naïf (ex: 2002)

        try {
            System.out.println("=== Démarrage du Serveur Polyvalent ===");

            // 2. Chargement unique de la Base de Données (Service)
            // Assure-toi que "prenoms.csv" est bien à la racine du projet
            String csvPath = "prenoms.csv";
            NameService service = new CsvNameService(csvPath);

            // -----------------------------------------------------------
            // A. DÉMARRAGE DU SERVEUR D'AGENTS (Mode Intelligent)
            // -----------------------------------------------------------
            // On écoute sur toutes les interfaces ("0.0.0.0") pour être sûr d'être accessible
            AgentServer server = new AgentServer("0.0.0.0", port);
            
            // On injecte le service dans l'annuaire pour que les agents puissent l'utiliser
            server.getNameServer().put("NameService", service);
            
            server.start(); // Lance son propre thread d'écoute
            System.out.println(">> [Agent] Serveur prêt sur le port " + port);


            // -----------------------------------------------------------
            // B. DÉMARRAGE DU SERVEUR RPC (Mode Naïf / Comparatif)
            // -----------------------------------------------------------
            // On lance ça dans un thread séparé pour ne pas bloquer le main
            new Thread(() -> startRpcServer(rpcPort, service)).start();
            System.out.println(">> [RPC]   Serveur 'Naïf' prêt sur le port " + rpcPort);
            
        } catch (Exception e) {
            System.err.println("Erreur critique au démarrage :");
            e.printStackTrace();
        }
    }

    // --- Méthodes pour le serveur RPC (Simulation RMI/HTTP basique) ---

    private static void startRpcServer(int port, NameService service) {
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                // On attend une connexion (bloquant)
                Socket client = ss.accept();
                // On traite la requête dans un thread pour gérer plusieurs clients si besoin
                new Thread(() -> handleRpc(client, service)).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur sur le serveur RPC : " + e.getMessage());
        }
    }

    private static void handleRpc(Socket socket, NameService service) {
        try (
            Socket s = socket;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true)
        ) {
            // Lecture de la requête
            String request = in.readLine();
            
            if (request != null) {
                int result = 0;

                // 1. Cas du test de performance (par numéro de ligne)
                // Protocole : "LINE:150"
                if (request.startsWith("LINE:")) {
                    try {
                        int lineIndex = Integer.parseInt(request.substring(5));
                        result = service.getCountByLine(lineIndex);
                    } catch (NumberFormatException e) {
                        System.err.println("Format de ligne invalide : " + request);
                    }
                } 
                // 2. Cas classique (par Nom et Année)
                // Protocole : "MARIE;1990"
                else {
                    String[] parts = request.split(";");
                    if (parts.length >= 2) {
                        try {
                            String name = parts[0];
                            int year = Integer.parseInt(parts[1]);
                            result = service.getCount(year, name);
                        } catch (NumberFormatException e) {
                            // Ignorer
                        }
                    }
                }

                // Envoi de la réponse
                out.println(result);
            }

        } catch (Exception e) {
            // Ignorer les erreurs de connexion ponctuelles
        }
    }
}