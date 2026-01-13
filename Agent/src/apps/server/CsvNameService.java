package apps.server;

import platform.service.NameService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList; 
import java.util.List;      


public class CsvNameService implements NameService {

    //Une liste pour accéder aux données par numéro de ligne
    private final List<Integer> rawCounts = new ArrayList<>();

    public CsvNameService(String csvFilePath) {
        System.out.println("Chargement du fichier CSV : " + csvFilePath + " ...");
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                try {
                    int count = Integer.parseInt(parts[3]);                     
                    rawCounts.add(count);

                } catch (Exception e) {
                    // Ignorer erreurs
                }
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCountByLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= rawCounts.size()) {
            return 0; // Sécurité si on demande une ligne qui n'existe pas
        }
        return rawCounts.get(lineNumber);
    }
}