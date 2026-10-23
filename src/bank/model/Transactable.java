package bank.model;

import bank.exception.BankException;
import bank.exception.InvalidInputException;

/** ABSTRACTION: the basic contract every money-holding object must honour. */
public interface Transactable {
    void deposit(double amount) throws InvalidInputException;
    void withdraw(double amount) throws BankException;
    double getBalance();
}
