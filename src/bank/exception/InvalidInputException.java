package bank.exception;

/** Raised when user input is missing, malformed, or out of range. */
public class InvalidInputException extends BankException {
    private static final long serialVersionUID = 1L;
    public InvalidInputException(String message) { super(message); }
}
