package apps.server;

import platform.service.NameService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CsvNameService implements NameService {

    // Structure de données : Map<Année, Map<Prénom, Nombre>>
    // Exemple : 1900 -> { "MARIE" -> 450, "JEAN" -> 300 }
    private final Map<Integer, Map<String, Integer>> database = new HashMap<>();

    public CsvNameService(String csvFilePath) {
        System.out.println("Chargement du fichier CSV : " + csvFilePath + " ...");
        long start = System.currentTimeMillis();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // On saute la première ligne si c'est l'entête
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                // Structure : sexe;preusuel;annais;nombre
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                try {
                    String name = parts[1].trim().toUpperCase(); // On normalise en MAJ
                    
                    // Gestion du cas "XXXX" pour l'année dans certains fichiers INSEE
                    if (parts[2].equals("XXXX")) continue;
                    int year = Integer.parseInt(parts[2]);
                    
                    int count = Integer.parseInt(parts[3]);

                    // On range dans la Map
                    database.putIfAbsent(year, new HashMap<>());
                    Map<String, Integer> namesInYear = database.get(year);
                    
                    // On additionne (cas des prénoms mixtes ou lignes multiples)
                    namesInYear.put(name, namesInYear.getOrDefault(name, 0) + count);

                } catch (NumberFormatException e) {
                    // Ignorer les lignes malformées
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("Base de données chargée en " + (end - start) + "ms (" + database.size() + " années).");

        } catch (Exception e) {
            System.err.println("Erreur de chargement du CSV : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getCount(int year, String name) {
        if (!database.containsKey(year)) return 0;
        
        // On cherche le prénom en MAJuscule
        return database.get(year).getOrDefault(name.toUpperCase(), 0);
    }
}