package bank.exception;

/** Base class of all checked errors raised by the banking system. */
public class BankException extends Exception {
    private static final long serialVersionUID = 1L;
    public BankException(String message) { super(message); }
}
