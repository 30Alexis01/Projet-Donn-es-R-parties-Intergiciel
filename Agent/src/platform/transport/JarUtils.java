package platform.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


 //lit un jar  et extrait les classes sous forme de bytes
 
public class JarUtils {

    // Lit un fichier jar et renvoie son contenu sous forme de tableau d'octets
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

        // Lecture complète du fichier 
        try (FileInputStream in = new FileInputStream(f)) {
            int bytesRead = 0;

            while (bytesRead < bytes.length) {
                int numberOfBytesRead = in.read(bytes,bytesRead,bytes.length - bytesRead);

                if (numberOfBytesRead == -1) {
                    break;
                }

            bytesRead += numberOfBytesRead;
            }

            if (bytesRead != bytes.length) {
                throw new IOException("Could not read full jar (read " + bytesRead + " / " + bytes.length + ")");
            }
        }

        return bytes;
    }

    //Extrait les classes d'un JAR et renvoie une map : "pkg.Classe" -> bytecode
    public static Map<String, byte[]> extractClasses(byte[] jarContent) throws IOException {
    Map<String, byte[]> classBytes = new HashMap<>();

    try (JarInputStream jarInput =
                 new JarInputStream(new ByteArrayInputStream(jarContent))) {

        JarEntry jarEntry;
        while ((jarEntry = jarInput.getNextJarEntry()) != null) {

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                continue;
            }

            String className = jarEntry.getName()
                    .replace('/', '.')
                    .substring(0, jarEntry.getName().length() - 6);

            ByteArrayOutputStream classBuffer = new ByteArrayOutputStream();
            byte[] tempBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = jarInput.read(tempBuffer)) != -1) {
                classBuffer.write(tempBuffer, 0, bytesRead);
            }

            classBytes.put(className, classBuffer.toByteArray());
        }
    }

    return classBytes;
}

}
