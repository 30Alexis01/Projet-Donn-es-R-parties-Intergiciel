import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class NameServiceImpl extends UnicastRemoteObject implements NameService {


    //Liste qui va contenir toute la colonne "nombre" du CSV
    private final List<Integer> counts = new ArrayList<>();

    public NameServiceImpl(String csvPath) throws RemoteException {
        super();
        loadCsv(csvPath);
    }

    private void loadCsv(String csvPath) throws RemoteException {
    try {
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        br.readLine(); 

        //On remplit counts avec uniquement la colonne nombre de notre fichier CSV
        String line;
        while ((line = br.readLine()) != null) {
            counts.add(Integer.parseInt(line.split(";")[3]));
        }

        br.close();
    } catch (Exception e) {
        throw new RemoteException("Erreur lecture CSV");
    }
}


    @Override
    public int getNbLines() throws RemoteException {
        return counts.size();
    }

    @Override
    public int getCountByLine(int lineNumber) throws RemoteException {
        if (lineNumber < 0 || lineNumber >= counts.size()) {
            return 0;
        }
        return counts.get(lineNumber);
    }

    @Override
    public void printServer(String message ) throws RemoteException{
        System.out.println(message);
    }
}
