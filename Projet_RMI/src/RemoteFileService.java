import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFileService extends Remote {
    //Télécharge un fichier depuis le serveur
    byte[] downloadFile(String fileName) throws RemoteException;

    void printServer(String message) throws RemoteException;
}