package hrms.exception;

/** Thrown when an input value is blank or otherwise invalid (BR2, BR11). */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
