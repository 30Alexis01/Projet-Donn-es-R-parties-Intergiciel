package platform.common;

import java.io.IOException;

/**
 * Exception levée si le protocole réseau n'est pas respecté.
 * Exemple:
 * - magic incorrect
 * - tailles négatives
 * - données incomplètes
 *
 * Je la fais hériter de IOException car ça reste une erreur de lecture réseau.
 */
public class ProtocolException extends IOException {

    private static final long serialVersionUID = 1L;

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
