package bank.model;

/** ABSTRACTION: implemented only by accounts that earn interest. */
public interface InterestBearing {
    double getAnnualInterestRate();
    double calculateMonthlyInterest();
}
