package platform.common;

// Exception levée quand un déplacement d'agent échoue.

public class MoveException extends Exception {

    private static final long serialVersionUID = 1L;

    public MoveException(String message) {
        super(message);
    }

    public MoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
