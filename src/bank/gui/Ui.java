package bank.gui;

import bank.exception.BankException;
import java.awt.Component;
import javax.swing.JOptionPane;

/** Small helpers for consistent dialogs and error handling. */
final class Ui {
    private Ui() { }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** A GUI action that may throw; known errors show their message, unknown ones a safe one. */
    interface Action { void run() throws BankException; }

    static void guard(Component parent, Action action) {
        try {
            action.run();
        } catch (BankException e) {
            error(parent, e.getMessage());
        } catch (RuntimeException e) {
            error(parent, "Unexpected error: " + e.getMessage());
        }
    }
}
