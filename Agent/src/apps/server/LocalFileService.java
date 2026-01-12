package apps.server;

import platform.service.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LocalFileService implements FileService {

    private final String rootDirectory;

    public LocalFileService(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public byte[] getFileContent(String fileName) throws IOException {
        // Construction du chemin complet
        File file = new File(rootDirectory, fileName);
        
        System.out.println("[LocalFileService] Lecture demandée : " + file.getAbsolutePath());

        if (!file.exists()) {
            throw new IOException("Fichier introuvable : " + fileName);
        }

        // Lecture de tous les octets du fichier
        return Files.readAllBytes(file.toPath());
    }
}