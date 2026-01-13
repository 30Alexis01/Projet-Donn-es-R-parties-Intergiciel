package platform.common;

import java.io.Serializable;

//En-tête utilisé lors de la migration d'un agent.

public class MigrationHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    // Signature du protocole 
    public final int magic;

    // Taille du JAR en octets
    public final int jarSize;

    // Taille des données sérialisées de l'agent (en octets)
    public final int dataSize;

    // Nom de la classe principale de l'agent 
    public final String mainClassName;

    public MigrationHeader(int magic, int jarSize, int dataSize, String mainClassName) {
        this.magic = magic;
        this.jarSize = jarSize;
        this.dataSize = dataSize;
        this.mainClassName = mainClassName;
    }

    @Override
    public String toString() {
        return "MigrationHeader{magic=" + magic
                + ", jarSize=" + jarSize
                + ", dataSize=" + dataSize
                + ", mainClassName='" + mainClassName + "'}";
    }

    // Construit un int "magic" à partir de 4 caractères (ex : "AGNT")
    public static int magicFromString(String s4) {
        if (s4 == null || s4.length() != 4) {
            throw new IllegalArgumentException("Magic doit faire 4 caractères");
        }

        int b0 = (byte) s4.charAt(0);
        int b1 = (byte) s4.charAt(1);
        int b2 = (byte) s4.charAt(2);
        int b3 = (byte) s4.charAt(3);

        return ((b0 & 0xFF) << 24)
             | ((b1 & 0xFF) << 16)
             | ((b2 & 0xFF) << 8)
             |  (b3 & 0xFF);
    }
}
