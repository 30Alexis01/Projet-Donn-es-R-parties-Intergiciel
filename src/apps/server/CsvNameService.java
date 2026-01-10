package apps.server;

import platform.service.NameService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList; // Import nécessaire
import java.util.HashMap;
import java.util.List;      // Import nécessaire
import java.util.Map;

public class CsvNameService implements NameService {

    private final Map<Integer, Map<String, Integer>> database = new HashMap<>();
    
    // NOUVEAU : Une liste simple pour accéder aux données par numéro de ligne
    private final List<Integer> rawCounts = new ArrayList<>();

    public CsvNameService(String csvFilePath) {
        System.out.println("Chargement du fichier CSV : " + csvFilePath + " ...");
        long start = System.currentTimeMillis();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                try {
                    String name = parts[1].trim().toUpperCase();
                    int count = Integer.parseInt(parts[3]);

                    // 1. Remplissage de l'ancienne base (Map)
                    if (!parts[2].equals("XXXX")) {
                        int year = Integer.parseInt(parts[2]);
                        database.putIfAbsent(year, new HashMap<>());
                        Map<String, Integer> namesInYear = database.get(year);
                        namesInYear.put(name, namesInYear.getOrDefault(name, 0) + count);
                    }
                    
                    // 2. NOUVEAU : Remplissage de la liste séquentielle
                    rawCounts.add(count);

                } catch (Exception e) {
                    // Ignorer erreurs
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("Chargé " + rawCounts.size() + " lignes en " + (end - start) + "ms.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount(int year, String name) {
        if (!database.containsKey(year)) return 0;
        return database.get(year).getOrDefault(name.toUpperCase(), 0);
    }

    // NOUVELLE IMPLÉMENTATION
    @Override
    public int getCountByLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= rawCounts.size()) {
            return 0; // Sécurité si on demande une ligne qui n'existe pas
        }
        return rawCounts.get(lineNumber);
    }
}