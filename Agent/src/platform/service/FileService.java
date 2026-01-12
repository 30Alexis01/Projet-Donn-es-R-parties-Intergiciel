package platform.service;

import java.io.IOException;

public interface FileService {
    /**
     * Lit un fichier et retourne son contenu en tableau d'octets.
     * @param fileName Le nom du fichier (ex: "doc_0.txt")
     * @return Les octets du fichier
     * @throws IOException Si le fichier n'existe pas ou erreur lecture
     */
    byte[] getFileContent(String fileName) throws IOException;
}