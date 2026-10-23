package bank.model;

import bank.exception.BankException;
import bank.exception.InsufficientFundsException;
import bank.exception.InvalidInputException;
import bank.util.Money;
import bank.util.Validator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ABSTRACTION  : abstract parent class; subclasses must define type, available funds, month-end rules.
 * ENCAPSULATION: balance and history are private; they change only through validated methods.
 * INHERITANCE  : SavingsAccount, CheckingAccount and FixedDepositAccount extend this class.
 */
public abstract class Account implements Transactable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final Customer owner;
    private double balance;
    private final List<Transaction> history = new ArrayList<>();

    protected Account(String accountNumber, Customer owner, double openingDeposit) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = Money.round(openingDeposit);
        record(Transaction.Type.DEPOSIT, openingDeposit, "Opening deposit");
    }

    // ---- getters only: no one can overwrite the balance directly ----
    public String getAccountNumber() { return accountNumber; }
    public Customer getOwner()       { return owner; }
    @Override public double getBalance() { return balance; }
    public List<Transaction> getHistory() { return Collections.unmodifiableList(history); }

    // ---- shared behaviour (inherited as-is) ----
    @Override
    public void deposit(double amount) throws InvalidInputException {
        deposit(amount, Transaction.Type.DEPOSIT, "Cash deposit");
    }

    public void deposit(double amount, Transaction.Type type, String note) throws InvalidInputException {
        Validator.requirePositive(amount);
        balance = Money.round(balance + amount);
        record(type, amount, note);
    }

    @Override
    public void withdraw(double amount) throws BankException {
        withdraw(amount, Transaction.Type.WITHDRAWAL, "Cash withdrawal");
    }

    /** Subclasses may OVERRIDE this to add their own withdrawal rules (polymorphism). */
    public void withdraw(double amount, Transaction.Type type, String note) throws BankException {
        Validator.requirePositive(amount);
        if (amount > getAvailableFunds()) {
            throw new InsufficientFundsException("Insufficient funds. Available: "
                    + Money.format(getAvailableFunds()));
        }
        balance = Money.round(balance - amount);
        record(type, amount, note);
    }

    /** For subclasses applying interest or fees. */
    protected void adjustBalance(double delta, Transaction.Type type, String note) {
        balance = Money.round(balance + delta);
        record(type, Math.abs(delta), note);
    }

    private void record(Transaction.Type type, double amount, String note) {
        history.add(new Transaction(type, amount, balance, note));
    }

    // ---- POLYMORPHIC methods: every subclass answers differently ----
    public abstract String getAccountType();
    public abstract double getAvailableFunds();
    /** Applies interest and/or fees; returns a one-line description of what happened. */
    public abstract String applyMonthEnd();

    @Override
    public String toString() {
        return accountNumber + " - " + owner.getName() + " (" + getAccountType() + ")";
    }
}
