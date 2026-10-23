package bank.util;

/** Formatting and rounding helpers for money values. */
public final class Money {
    private Money() { }

    public static String format(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    public static double round(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
