import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFileService extends Remote {
    /**
     * Télécharge un fichier depuis le serveur.
     * @param fileName Nom du fichier (ex: "doc_0.txt")
     * @return Le contenu du fichier en octets
     * @throws RemoteException Obligatoire pour RMI
     */
    byte[] downloadFile(String fileName) throws RemoteException;

    void printServer(String message) throws RemoteException;
}