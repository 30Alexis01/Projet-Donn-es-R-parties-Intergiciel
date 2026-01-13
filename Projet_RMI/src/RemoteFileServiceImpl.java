import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteFileServiceImpl extends UnicastRemoteObject implements RemoteFileService {

    private final String rootDir;

    public RemoteFileServiceImpl(String rootDir) throws RemoteException {
        super();
        this.rootDir = rootDir;
    }

    @Override
    public byte[] downloadFile(String fileName) throws RemoteException {
        try {
            // On cherche le fichier dans le dossier racine configuré (server_files)
            File f = new File(rootDir, fileName);
            
            // Lecture des octets (I/O disque)
            return Files.readAllBytes(f.toPath());
            
        } catch (IOException e) {
            // En RMI, on encapsule les erreurs serveur dans une RemoteException
            throw new RemoteException("Erreur lecture fichier: " + fileName, e);
        }
    }

    @Override
    public void printServer(String message ) throws RemoteException{
        System.out.println(message);
    }
}