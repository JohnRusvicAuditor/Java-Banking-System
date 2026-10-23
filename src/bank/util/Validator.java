package bank.util;

import bank.exception.InvalidInputException;

/** Central place for all input validation rules. */
public final class Validator {
    public static final double MAX_AMOUNT = 1_000_000_000.00;

    private Validator() { }

    public static String requireText(String field, String value) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(field + " is required.");
        }
        return value.trim();
    }

    public static String requireName(String value) throws InvalidInputException {
        String v = requireText("Name", value);
        if (!v.matches("[\\p{L} .'-]{2,60}")) {
            throw new InvalidInputException(
                "Name must be 2-60 characters and contain only letters, spaces, . ' or -.");
        }
        return v;
    }

    public static String requirePhone(String value) throws InvalidInputException {
        String v = requireText("Contact number", value);
        if (!v.matches("\\+?[0-9]{7,15}")) {
            throw new InvalidInputException("Contact number must contain 7-15 digits (optional leading +).");
        }
        return v;
    }

    public static String requireEmail(String value) throws InvalidInputException {
        String v = requireText("Email", value);
        if (!v.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            throw new InvalidInputException("Email address format is invalid.");
        }
        return v;
    }

    /** Parses text typed by the user into a monetary amount. */
    public static double parseAmount(String field, String raw) throws InvalidInputException {
        String v = requireText(field, raw).replace(",", "");
        double amount;
        try {
            amount = Double.parseDouble(v);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(field + " must be a valid number.");
        }
        requirePositive(amount);
        return amount;
    }

    public static double requirePositive(double amount) throws InvalidInputException {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new InvalidInputException("Amount is not a valid number.");
        }
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        if (amount > MAX_AMOUNT) {
            throw new InvalidInputException("Amount exceeds the allowed maximum.");
        }
        if (Math.abs(amount * 100 - Math.round(amount * 100)) > 1e-6) {
            throw new InvalidInputException("Amount can have at most 2 decimal places.");
        }
        return amount;
    }
}
