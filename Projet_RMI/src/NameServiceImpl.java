import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class NameServiceImpl extends UnicastRemoteObject implements NameService {

    private final List<Integer> counts = new ArrayList<>();

    public NameServiceImpl(String csvPath) throws RemoteException {
        super();
        loadCsv(csvPath);
    }

    private void loadCsv(String csvPath) throws RemoteException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;

            br.readLine(); // Skip header (comme CsvNameService)

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                try {
                    // Comme CsvNameService : on parse juste la colonne "nombre"
                    int count = Integer.parseInt(parts[3].trim());
                    counts.add(count);
                } catch (Exception e) {
                    // Ignorer erreurs (comme CsvNameService)
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Erreur chargement CSV", e);
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
}
