package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.BankException;
import bank.exception.InvalidInputException;
import bank.model.*;
import bank.util.Money;
import bank.util.Validator;
import java.io.Serializable;
import java.util.*;

/** Business logic. The GUI talks to this class, never to the data structures directly. */
public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextNumber = 1001;

    public Account openAccount(AccountType type, String name, String phone, String email, double opening)
            throws BankException {
        if (type == null) throw new InvalidInputException("Please select an account type.");
        Customer customer = new Customer(name, phone, email);
        Validator.requirePositive(opening);
        if (opening < type.getMinimumOpeningDeposit()) {
            throw new InvalidInputException(type + " accounts require an opening deposit of at least "
                    + Money.format(type.getMinimumOpeningDeposit()) + ".");
        }
        String number = "ACC-" + nextNumber++;
        Account account;
        switch (type) {   // factory: parent-typed variable, child objects
            case SAVINGS:       account = new SavingsAccount(number, customer, opening); break;
            case CHECKING:      account = new CheckingAccount(number, customer, opening); break;
            default:            account = new FixedDepositAccount(number, customer, opening); break;
        }
        accounts.put(number, account);
        return account;
    }

    public Account getAccount(String number) throws AccountNotFoundException {
        Account a = accounts.get(number);
        if (a == null) throw new AccountNotFoundException(number);
        return a;
    }

    public Collection<Account> getAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    public List<Account> search(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Account> result = new ArrayList<>();
        for (Account a : accounts.values()) {
            if (q.isEmpty() || a.getAccountNumber().toLowerCase().contains(q)
                    || a.getOwner().getName().toLowerCase().contains(q)
                    || a.getAccountType().toLowerCase().contains(q)) {
                result.add(a);
            }
        }
        return result;
    }

    public void closeAccount(String number) throws BankException {
        Account a = getAccount(number);
        if (a.getBalance() != 0) {
            throw new InvalidInputException("Only accounts with a zero balance can be closed. Current balance: "
                    + Money.format(a.getBalance()));
        }
        accounts.remove(number);
    }

    public void transfer(String fromNo, String toNo, double amount) throws BankException {
        if (fromNo == null || toNo == null) throw new InvalidInputException("Select both accounts.");
        if (fromNo.equals(toNo)) throw new InvalidInputException("Cannot transfer to the same account.");
        Account from = getAccount(fromNo);
        Account to = getAccount(toNo);
        Validator.requirePositive(amount);
        from.withdraw(amount, Transaction.Type.TRANSFER_OUT, "Transfer to " + toNo);
        to.deposit(amount, Transaction.Type.TRANSFER_IN, "Transfer from " + fromNo);
    }

    /** POLYMORPHISM: one call, each account type applies its own rule. */
    public List<String> applyMonthEndToAll() {
        List<String> log = new ArrayList<>();
        for (Account a : accounts.values()) {
            log.add(a.applyMonthEnd());
        }
        return log;
    }

    public double getTotalDeposits() {
        double total = 0;
        for (Account a : accounts.values()) total += a.getBalance();
        return Money.round(total);
    }
}
