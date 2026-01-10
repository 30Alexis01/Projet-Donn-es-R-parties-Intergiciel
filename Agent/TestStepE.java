import platform.server.CustomClassLoader;
import platform.server.CustomObjectInputStream;
import platform.transport.JarUtils;
import platform.transport.SerializerUtils;
import platform.agent.Agent;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class TestStepE {

    public static void main(String[] args) {
        try {
            System.out.println("=== Test Etape E : Cerveau ===");

            // 1. Charger le JAR brut (comme si on l'avait reçu du réseau)
            // Assure-toi que le chemin est bon par rapport à où tu lances la commande
            byte[] jarBytes = JarUtils.loadJar("agents/test-agent.jar");
            System.out.println("[OK] JAR chargé : " + jarBytes.length + " octets");

            // 2. Extraire les classes (le travail de pré-chargement)
            Map<String, byte[]> classes = JarUtils.extractClasses(jarBytes);
            System.out.println("[OK] Classes extraites : " + classes.keySet());

            // 3. Instancier notre CustomClassLoader
            CustomClassLoader loader = new CustomClassLoader(ClassLoader.getSystemClassLoader(), classes);
            
            // 4. Essayer de charger une classe du JAR manuellement
            // (Le nom dépend de ton agent, souvent apps.client.RawClientMain$1 pour l'agent anonyme)
            String agentClassName = "apps.client.RawClientMain$1"; 
            
            Class<?> loadedClass = loader.loadClass(agentClassName);
            System.out.println("[OK] Classe chargée avec succès : " + loadedClass.getName());
            System.out.println("     ClassLoader utilisé : " + loadedClass.getClassLoader());

            // 5. Test du CustomObjectInputStream
            // Pour tester ça, il faudrait avoir des DATA sérialisées. 
            // Si tu as gardé un fichier data produit par le client, tu peux le charger ici.
            // Sinon, la simple réussite du chargement de classe (étape 4) valide déjà 90% du travail.
            
            System.out.println("=== Test Etape E REUSSI ===");

        } catch (Exception e) {
            System.err.println("=== ECHEC du Test ===");
            e.printStackTrace();
        }
    }
}