package platform.service;

import java.io.IOException;

public interface FileService {
    //Lit un fichier et retourne son contenu en tableau d'octets.
    byte[] getFileContent(String fileName) throws IOException;
}