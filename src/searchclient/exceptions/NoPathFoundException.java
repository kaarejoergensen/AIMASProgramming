package searchclient.exceptions;

public class NoPathFoundException extends Exception {
    public NoPathFoundException() {
        super();
    }

    public NoPathFoundException(String message) {
        super(message);
    }

    public NoPathFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
