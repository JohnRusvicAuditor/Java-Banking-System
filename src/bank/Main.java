package bank;

import bank.gui.MainFrame;
import bank.service.Bank;
import bank.service.BankStorage;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* fall back to default look and feel */ }

        BankStorage storage = new BankStorage(Paths.get("data", "bank.dat"));
        Bank bank = storage.load();
        SwingUtilities.invokeLater(() -> new MainFrame(bank, storage).setVisible(true));
    }
}
