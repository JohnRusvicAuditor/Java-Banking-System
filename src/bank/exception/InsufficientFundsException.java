package bank.exception;

/** Raised when a withdrawal or transfer exceeds the funds available. */
public class InsufficientFundsException extends BankException {
    private static final long serialVersionUID = 1L;
    public InsufficientFundsException(String message) { super(message); }
}
