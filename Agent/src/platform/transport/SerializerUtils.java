package platform.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Outils pour sérialiser un objet Java (ici l'Agent) en byte[].
 * On ne fait que la sérialisation côté envoi.
 */
public class SerializerUtils {

    /**
     * Sérialise un objet Serializable en tableau d'octets.
     */
    public static byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        // ObjectOutputStream écrit l'objet + métadonnées de sérialisation
        oos.writeObject(obj);
        oos.flush();

        return bos.toByteArray();
    }
}
