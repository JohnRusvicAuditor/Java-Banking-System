package bank.model;

import bank.util.Money;

/** INHERITANCE + POLYMORPHISM: allows an overdraft; charges a monthly fee on low balances. */
public class CheckingAccount extends Account {
    private static final long serialVersionUID = 1L;
    public static final double OVERDRAFT_LIMIT = 5000.00;
    public static final double LOW_BALANCE_THRESHOLD = 5000.00;
    public static final double MONTHLY_FEE = 50.00;

    public CheckingAccount(String accountNumber, Customer owner, double openingDeposit) {
        super(accountNumber, owner, openingDeposit);
    }

    @Override public String getAccountType() { return "Checking"; }

    @Override public double getAvailableFunds() {
        return getBalance() + OVERDRAFT_LIMIT;
    }

    @Override public String applyMonthEnd() {
        if (getBalance() < LOW_BALANCE_THRESHOLD) {
            adjustBalance(-MONTHLY_FEE, Transaction.Type.FEE, "Low-balance monthly fee");
            return getAccountNumber() + ": fee charged " + Money.format(MONTHLY_FEE);
        }
        return getAccountNumber() + ": no fee (balance is above " + Money.format(LOW_BALANCE_THRESHOLD) + ")";
    }
}
