import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameService extends Remote {
    // Renvoie le nombre de ligne du fichier csv
    int getNbLines() throws RemoteException;

    
    // Renvoie la valeur "nombre" de la ligne lineNumber du fichier csv
    int getCountByLine(int lineNumber) throws RemoteException;

    void printServer(String message) throws RemoteException;
}
