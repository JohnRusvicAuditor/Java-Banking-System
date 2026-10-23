package bank.model;

/** The kinds of accounts the bank offers, with their opening-deposit rule. */
public enum AccountType {
    SAVINGS("Savings", 500.00),
    CHECKING("Checking", 1000.00),
    FIXED_DEPOSIT("Fixed Deposit", 5000.00);

    private final String label;
    private final double minimumOpeningDeposit;

    AccountType(String label, double minimumOpeningDeposit) {
        this.label = label;
        this.minimumOpeningDeposit = minimumOpeningDeposit;
    }

    public double getMinimumOpeningDeposit() { return minimumOpeningDeposit; }

    @Override public String toString() { return label; }
}
