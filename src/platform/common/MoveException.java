package platform.common;

/**
 * Exception levée quand un déplacement d'agent échoue.
 * Exemples:
 * - impossible de se connecter au node cible
 * - erreur d'envoi
 * - erreur protocole
 */
public class MoveException extends Exception {

    private static final long serialVersionUID = 1L;

    public MoveException(String message) {
        super(message);
    }

    public MoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
