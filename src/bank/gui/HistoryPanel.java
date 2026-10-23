package bank.gui;

import bank.model.Account;
import bank.model.Transaction;
import bank.service.Bank;
import bank.util.Money;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Feature: view transaction history of an account. */
class HistoryPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Bank bank;
    private final AccountComboBox accountBox = new AccountComboBox();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date / Time", "Type", "Amount", "Balance After", "Note"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    HistoryPanel(Bank bank) {
        this.bank = bank;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Account:"));
        top.add(accountBox);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        accountBox.addActionListener(e -> load());
        refresh();
    }

    private void load() {
        model.setRowCount(0);
        Account a = accountBox.getSelected();
        if (a == null) return;
        for (Transaction t : a.getHistory()) {
            model.addRow(new Object[]{t.getTimestamp().format(FMT), t.getType(),
                    Money.format(t.getAmount()), Money.format(t.getBalanceAfter()), t.getNote()});
        }
    }

    @Override public void refresh() {
        accountBox.reload(bank.getAccounts());
        load();
    }
}
