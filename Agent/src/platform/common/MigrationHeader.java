package platform.common;

import java.io.Serializable;

/**
 * Header du protocole de migration.
 *
 * Idée: avant d'envoyer le JAR et les DATA, on envoie un en-tête qui dit:
 * - "c'est bien un message agent" (magic)
 * - version du protocole
 * - taille du JAR
 * - taille des données sérialisées
 * - (optionnel) nom de la classe principale de l'agent
 *
 * Ici c'est juste une structure de données (pas de réseau dedans).
 */
public class MigrationHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Signature du protocole (ex: 'A''G''N''T') */
    public final int magic;


    /** Taille du JAR en octets */
    public final int jarSize;

    /** Taille des données sérialisées en octets */
    public final int dataSize;

    /** Nom de la classe principale de l'agent (optionnel mais pratique) */
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

    /**
     * Petite méthode utilitaire pour créer un magic int à partir de 4 caractères.
     * Exemple : magicFromString("AGNT")
     *
     * (Tu peux ne pas l'utiliser si tu préfères mettre un int constant ailleurs.)
     */
    public static int magicFromString(String s4) {
        if (s4 == null || s4.length() != 4) {
            throw new IllegalArgumentException("Magic doit faire 4 caractères");
        }
        // On packe 4 chars sur 32 bits (1 char = 8 bits ici car on cast en byte)
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
