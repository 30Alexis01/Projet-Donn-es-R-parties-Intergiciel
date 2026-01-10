package platform.transport;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import platform.common.MigrationHeader;
import platform.common.Node;

/**
 * AgentSender = la "poste".
 * Ouvre une socket vers un AgentServer et envoie :
 * Header -> JAR -> DATA
 */
public class AgentSender {

    /** Magic + version du protocole (tu peux changer, mais il faut être cohérent serveur/client) */
    public static final int PROTOCOL_MAGIC = MigrationHeader.magicFromString("AGNT");
    public static final int PROTOCOL_VERSION = 1;

    /**
     * Envoie un agent vers un node cible.
     *
     * @param target destination (host,port)
     * @param jarBytes le JAR contenant le code (bytes)
     * @param dataBytes l'objet agent sérialisé (bytes)
     * @param mainClassName nom de la classe principale de l'agent (pratique pour debug)
     */
    public static void send(Node target, byte[] jarBytes, byte[] dataBytes, String mainClassName) throws IOException {

        // 1) Construire le header (structure logique)
        MigrationHeader header = new MigrationHeader(
                PROTOCOL_MAGIC,
                PROTOCOL_VERSION,
                jarBytes.length,
                dataBytes.length,
                mainClassName
        );

        // 2) Ouvrir une connexion TCP vers le serveur d'agents cible
        Socket socket = new Socket(target.host, target.port);

        try {
            // Buffered pour éviter d'envoyer trop petit morceau par morceau
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            // 3) Envoyer le header (ordre IMPORTANT)
            out.writeInt(header.magic);
            out.writeInt(header.version);
            out.writeInt(header.jarSize);
            out.writeInt(header.dataSize);
            out.writeUTF(header.mainClassName != null ? header.mainClassName : "");

            // 4) Envoyer le JAR
            out.write(jarBytes);

            // 5) Envoyer les DATA (agent sérialisé)
            out.write(dataBytes);

            // 6) Forcer l'envoi
            out.flush();

        } finally {
            socket.close();
        }
    }
}
