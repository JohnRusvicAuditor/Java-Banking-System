package bank.model;

import bank.exception.BankException;
import bank.exception.InsufficientFundsException;
import bank.util.Money;
import bank.util.Validator;

/** INHERITANCE + POLYMORPHISM: highest interest, but withdrawals carry a 2% early-withdrawal penalty. */
public class FixedDepositAccount extends Account implements InterestBearing {
    private static final long serialVersionUID = 1L;
    public static final double ANNUAL_RATE = 0.06;
    public static final double PENALTY_RATE = 0.02;

    public FixedDepositAccount(String accountNumber, Customer owner, double openingDeposit) {
        super(accountNumber, owner, openingDeposit);
    }

    @Override public String getAccountType() { return "Fixed Deposit"; }

    @Override public double getAvailableFunds() { return getBalance(); }

    @Override public double getAnnualInterestRate() { return ANNUAL_RATE; }

    @Override public double calculateMonthlyInterest() {
        return Money.round(getBalance() * ANNUAL_RATE / 12);
    }

    /** OVERRIDES the parent rule: amount + penalty must be covered. */
    @Override
    public void withdraw(double amount, Transaction.Type type, String note) throws BankException {
        Validator.requirePositive(amount);
        double penalty = Money.round(amount * PENALTY_RATE);
        if (amount + penalty > getBalance()) {
            throw new InsufficientFundsException("Insufficient funds. A " + (int) (PENALTY_RATE * 100)
                    + "% early-withdrawal penalty (" + Money.format(penalty) + ") also applies.");
        }
        super.withdraw(amount, type, note);
        adjustBalance(-penalty, Transaction.Type.FEE, "Early withdrawal penalty (2%)");
    }

    @Override public String applyMonthEnd() {
        double interest = calculateMonthlyInterest();
        adjustBalance(interest, Transaction.Type.INTEREST, "Monthly interest (6% p.a.)");
        return getAccountNumber() + ": interest credited " + Money.format(interest);
    }
}
