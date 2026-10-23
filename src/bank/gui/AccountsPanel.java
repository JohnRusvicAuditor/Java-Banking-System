package bank.gui;

import bank.exception.InvalidInputException;
import bank.model.Account;
import bank.model.AccountType;
import bank.service.Bank;
import bank.util.Money;
import bank.util.Validator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Features: open account, view accounts, search accounts, close account. */
class AccountsPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final Bank bank;
    private final Runnable onChange;

    private final JComboBox<AccountType> typeBox = new JComboBox<>(AccountType.values());
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField openingField = new JTextField();
    private final JTextField searchField = new JTextField(20);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Account No.", "Type", "Owner", "Contact", "Email", "Balance", "Available"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    AccountsPanel(Bank bank, Runnable onChange) {
        this.bank = bank;
        this.onChange = onChange;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        refresh();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Open a New Account"));
        form.add(new JLabel("Account type:"));   form.add(typeBox);
        form.add(new JLabel("Full name:"));      form.add(nameField);
        form.add(new JLabel("Contact number:")); form.add(phoneField);
        form.add(new JLabel("Email:"));          form.add(emailField);
        form.add(new JLabel("Opening deposit:")); form.add(openingField);
        JButton create = new JButton("Open Account");
        JButton clear = new JButton("Clear");
        create.addActionListener(e -> openAccount());
        clear.addActionListener(e -> clearForm());
        form.add(create); form.add(clear);
        return form;
    }

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton find = new JButton("Search");
        JButton all = new JButton("Show All");
        find.addActionListener(e -> fill(bank.search(searchField.getText())));
        searchField.addActionListener(e -> fill(bank.search(searchField.getText())));
        all.addActionListener(e -> { searchField.setText(""); refresh(); });
        search.add(new JLabel("Search (number, name or type):"));
        search.add(searchField); search.add(find); search.add(all);
        p.add(search, BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close Selected Account");
        close.addActionListener(e -> closeSelected());
        p.add(close);
        return p;
    }

    private void openAccount() {
        Ui.guard(this, () -> {
            double opening = Validator.parseAmount("Opening deposit", openingField.getText());
            Account a = bank.openAccount((AccountType) typeBox.getSelectedItem(), nameField.getText(),
                    phoneField.getText(), emailField.getText(), opening);
            clearForm();
            onChange.run();
            Ui.info(this, "Account " + a.getAccountNumber() + " opened for " + a.getOwner().getName() + ".");
        });
    }

    private void closeSelected() {
        Ui.guard(this, () -> {
            int row = table.getSelectedRow();
            if (row < 0) throw new InvalidInputException("Select an account in the table first.");
            String number = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
            int ok = JOptionPane.showConfirmDialog(this, "Close account " + number + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                bank.closeAccount(number);
                onChange.run();
            }
        });
    }

    private void clearForm() {
        nameField.setText(""); phoneField.setText(""); emailField.setText(""); openingField.setText("");
        typeBox.setSelectedIndex(0);
    }

    private void fill(List<Account> accounts) {
        model.setRowCount(0);
        for (Account a : accounts) {
            model.addRow(new Object[]{a.getAccountNumber(), a.getAccountType(), a.getOwner().getName(),
                    a.getOwner().getPhone(), a.getOwner().getEmail(),
                    Money.format(a.getBalance()), Money.format(a.getAvailableFunds())});
        }
    }

    @Override public void refresh() { fill(bank.search(searchField.getText())); }
}
