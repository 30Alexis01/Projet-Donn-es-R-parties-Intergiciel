package apps.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NaiveClientMain {

    public static void main(String[] args) {
        // IP DE TON AMI
        String serverIp = "192.168.1.50"; 
        int rpcPort = 2002;
        int maxLines = 10000; // Le même nombre que l'agent

        System.out.println("=== Client Naïf (RMI) : " + maxLines + " requêtes ===");
        
        long globalStart = System.currentTimeMillis();
        long total = 0;

        try {
            for (int i = 0; i < maxLines; i++) {
                
                // Connexion à chaque itération (Simulation du pire cas / HTTP sans keep-alive)
                try (Socket s = new Socket(serverIp, rpcPort);
                     PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    
                    // Protocole : "LINE:Numéro"
                    out.println("LINE:" + i);
                    
                    String response = in.readLine();
                    if (response != null) {
                        total += Integer.parseInt(response);
                    }
                }
                
                // Petit log tous les 1000 pour voir que ça avance (sinon on croit que c'est planté)
                if (i % 1000 == 0) System.out.print(".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();

        long globalEnd = System.currentTimeMillis();
        
        System.out.println("------------------------------------------------");
        System.out.println("Résultat final : " + total);
        System.out.println("Temps total (Naïf) : " + (globalEnd - globalStart) + " ms");
        System.out.println("------------------------------------------------");
    }
}