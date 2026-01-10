package platform.transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Outils pour lire un fichier JAR en mémoire (byte[]).
 * Simple : on lit tout le fichier d'un coup.
 */
public class JarUtils {

    /**
     * Charge un fichier JAR depuis un chemin et retourne son contenu en bytes.
     *
     * @param jarPath chemin du fichier .jar (ex: "agents/agent.jar")
     */
    public static byte[] loadJar(String jarPath) throws IOException {
        File f = new File(jarPath);

        if (!f.exists()) {
            throw new IOException("Jar not found: " + jarPath);
        }

        long len = f.length();
        if (len > Integer.MAX_VALUE) {
            throw new IOException("Jar too big: " + len);
        }

        byte[] bytes = new byte[(int) len];

        FileInputStream in = new FileInputStream(f);
        try {
            int offset = 0;
            while (offset < bytes.length) {
                int r = in.read(bytes, offset, bytes.length - offset);
                if (r == -1) break;
                offset += r;
            }
            if (offset != bytes.length) {
                throw new IOException("Could not read full jar (read " + offset + " / " + bytes.length + ")");
            }
        } finally {
            in.close();
        }

        return bytes;
    }

    // ... imports nécessaires : java.io.ByteArrayInputStream, java.io.ByteArrayOutputStream, java.util.jar.JarInputStream, java.util.jar.JarEntry, java.util.HashMap, java.util.Map

    /**
     * Extrait toutes les classes d'un JAR (donné sous forme d'octets).
     * @return Une Map : "pkg.NomClasse" -> byte[] (bytecode)
     */
    public static java.util.Map<String, byte[]> extractClasses(byte[] jarContent) throws IOException {
        java.util.Map<String, byte[]> classes = new java.util.HashMap<>();
        
        try (java.util.jar.JarInputStream jis = new java.util.jar.JarInputStream(new java.io.ByteArrayInputStream(jarContent))) {
            java.util.jar.JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    
                    // 1. Convertir le chemin en nom de classe (ex: "com/foo/Bar.class" -> "com.foo.Bar")
                    String className = entry.getName()
                            .replace('/', '.')
                            .substring(0, entry.getName().length() - 6); // retire ".class"
                    
                    // 2. Lire les octets de l'entrée
                    java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int n;
                    while ((n = jis.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, n);
                    }
                    
                    classes.put(className, buffer.toByteArray());
                }
            }
        }
        return classes;
    }
}
