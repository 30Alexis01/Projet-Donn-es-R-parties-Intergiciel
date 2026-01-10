import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameService extends Remote {
    // Pour connaître combien de lignes il y a (sinon le client ne peut pas boucler proprement)
    int getNbLines() throws RemoteException;

    // API "bête" : 1 appel distant par ligne
    // -> le client demande au serveur si la ligne i correspond au filtre (prenom, annee)
    int getCountByLine(int lineNumber) throws RemoteException;
}
