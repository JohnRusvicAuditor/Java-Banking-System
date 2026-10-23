package bank.gui;

import bank.service.Bank;
import bank.service.BankStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/** Main window: hosts the tabs and keeps every screen in sync after a change. */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Bank bank;
    private final BankStorage storage;
    private final List<Refreshable> screens = new ArrayList<>();

    public MainFrame(Bank bank, BankStorage storage) {
        super("Java Banking System");
        this.bank = bank;
        this.storage = storage;

        Runnable onChange = this::refreshAll;
        JTabbedPane tabs = new JTabbedPane();
        add(tabs, "Accounts", new AccountsPanel(bank, onChange));
        add(tabs, "Transactions", new TransactionPanel(bank, onChange));
        add(tabs, "History", new HistoryPanel(bank));
        add(tabs, "Reports", new ReportPanel(bank, onChange));
        getContentPane().add(tabs);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
    }

    private <T extends JPanel & Refreshable> void add(JTabbedPane tabs, String title, T panel) {
        tabs.addTab(title, panel);
        screens.add(panel);   // POLYMORPHISM: stored and refreshed as Refreshable
    }

    private void refreshAll() {
        for (Refreshable r : screens) r.refresh();
        try {
            storage.save(bank);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save data: " + e.getMessage(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
