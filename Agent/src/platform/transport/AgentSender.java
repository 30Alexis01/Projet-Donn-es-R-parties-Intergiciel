package platform.transport;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import platform.common.MigrationHeader;
import platform.common.Node;


 //Ouvre une socket vers un AgentServer et envoie : Header , JAR , data
public class AgentSender {

    //Magic pour vérifier que c'est bien un agent que le serveur reçoit
    public static final int PROTOCOL_MAGIC = MigrationHeader.magicFromString("AGNT");

    
    //Envoie un agent vers un node cible.
    public static void send(Node target, byte[] jarBytes, byte[] dataBytes, String mainClassName) throws IOException {

        // 1) Construire le header (structure logique)
        MigrationHeader header = new MigrationHeader(
                PROTOCOL_MAGIC,
                jarBytes.length,
                dataBytes.length,
                mainClassName
        );

        // 2) Ouvrir une connexion TCP vers le serveur d'agents cible
        Socket socket = new Socket(target.host, target.port);

        try {
            // Buffered pour éviter d'envoyer trop petit morceau par morceau
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            // Envoyer le header qui contient les informations sur ce que va contenir l'agent
            out.writeInt(header.magic);
            out.writeInt(header.jarSize);
            out.writeInt(header.dataSize);
            out.writeUTF(header.mainClassName != null ? header.mainClassName : "");

            //Envoyer le JAR
            out.write(jarBytes);

            // Envoyer les DATA
            out.write(dataBytes);

            //Forcer l'envoi vers le socket
            out.flush();

        } finally {
            socket.close();
        }
    }
}
