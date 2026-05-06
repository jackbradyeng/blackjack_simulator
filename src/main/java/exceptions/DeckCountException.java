package exceptions;

public class DeckCountException extends RuntimeException {
    public DeckCountException(String message) {
        super(message);
    }
}
