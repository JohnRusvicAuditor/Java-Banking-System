package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** ENCAPSULATION: an immutable record of one balance change. */
public final class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST, FEE }

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String note;

    public Transaction(Type type, double amount, double balanceAfter, String note) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.note = note;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Type getType()               { return type; }
    public double getAmount()           { return amount; }
    public double getBalanceAfter()     { return balanceAfter; }
    public String getNote()             { return note; }
}
