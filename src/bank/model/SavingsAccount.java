package bank.model;

import bank.util.Money;

/** INHERITANCE + POLYMORPHISM: earns interest; must keep a minimum balance. */
public class SavingsAccount extends Account implements InterestBearing {
    private static final long serialVersionUID = 1L;
    public static final double ANNUAL_RATE = 0.03;
    public static final double MINIMUM_BALANCE = 500.00;

    public SavingsAccount(String accountNumber, Customer owner, double openingDeposit) {
        super(accountNumber, owner, openingDeposit);
    }

    @Override public String getAccountType() { return "Savings"; }

    @Override public double getAvailableFunds() {
        return Math.max(0, getBalance() - MINIMUM_BALANCE);
    }

    @Override public double getAnnualInterestRate() { return ANNUAL_RATE; }

    @Override public double calculateMonthlyInterest() {
        return Money.round(getBalance() * ANNUAL_RATE / 12);
    }

    @Override public String applyMonthEnd() {
        double interest = calculateMonthlyInterest();
        if (interest > 0) {
            adjustBalance(interest, Transaction.Type.INTEREST, "Monthly interest (3% p.a.)");
        }
        return getAccountNumber() + ": interest credited " + Money.format(interest);
    }
}
