package apps.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileGenerator {

    // --- CONFIGURATION DU TEST ---
    private static final String DIR_NAME = "server_files";
    private static final int NB_FILES = 100;      // 100 Fichiers
    private static final int FILE_SIZE_KB = 100;  // 100 Ko chacun

    public static void main(String[] args) {
        File dir = new File(DIR_NAME);
        
        // 1. Création du dossier
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Dossier créé : " + dir.getAbsolutePath());
            }
        } else {
             System.out.println("Le dossier existe déjà : " + dir.getAbsolutePath());
        }

        System.out.println("Génération de " + NB_FILES + " fichiers de " + FILE_SIZE_KB + " Ko...");

        // 2. Préparation du motif (texte répétitif)
        StringBuilder sb = new StringBuilder();
        String phrase = "Ceci est une donnée de test pour notre benchmark Agent vs RMI. ";
        
        // On construit un bloc de base de 1 Ko (1024 octets)
        while (sb.toString().getBytes(StandardCharsets.UTF_8).length < 1024) {
            sb.append(phrase);
            sb.append("\n");
        }
        // On coupe pile à 1024 octets pour être précis
        byte[] oneKbOfText = new byte[1024];
        byte[] source = sb.toString().getBytes(StandardCharsets.UTF_8);
        System.arraycopy(source, 0, oneKbOfText, 0, 1024);

        // 3. Écriture des fichiers
        long totalBytes = 0;
        for (int i = 0; i < NB_FILES; i++) {
            File file = new File(dir, "doc_" + i + ".txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                // On écrit X blocs de 1 Ko
                for (int k = 0; k < FILE_SIZE_KB; k++) {
                    fos.write(oneKbOfText);
                }
                totalBytes += file.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("------------------------------------------------");
        System.out.println("Terminé !");
        System.out.println("Fichiers générés : " + NB_FILES);
        System.out.println("Taille unitaire  : " + FILE_SIZE_KB + " Ko");
        System.out.println("Taille totale    : " + (totalBytes / 1024 / 1024) + " Mo");
        System.out.println("Localisation     : " + dir.getAbsolutePath());
    }
}