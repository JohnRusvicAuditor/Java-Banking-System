package bank.gui;

import bank.exception.InvalidInputException;
import bank.model.Account;
import bank.service.Bank;
import bank.util.Money;
import bank.util.Validator;
import java.awt.*;
import javax.swing.*;

/** Features: deposit, withdraw, transfer. */
class TransactionPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final Bank bank;
    private final Runnable onChange;
    private final AccountComboBox fromBox = new AccountComboBox();
    private final AccountComboBox toBox = new AccountComboBox();
    private final JTextField amountField = new JTextField(12);
    private final JLabel infoLabel = new JLabel(" ");

    TransactionPanel(Bank bank, Runnable onChange) {
        this.bank = bank;
        this.onChange = onChange;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBorder(BorderFactory.createTitledBorder("Process a Transaction"));
        form.add(new JLabel("Account:"));            form.add(fromBox);
        form.add(new JLabel("Amount:"));             form.add(amountField);
        form.add(new JLabel("Account details:"));    form.add(infoLabel);

        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");
        form.add(deposit); form.add(withdraw);

        form.add(new JLabel("Transfer to account:")); form.add(toBox);
        JButton transfer = new JButton("Transfer Funds");
        form.add(new JLabel()); form.add(transfer);

        deposit.addActionListener(e -> Ui.guard(this, () -> {
            Account a = selectedFrom();
            a.deposit(amount());
            done("Deposited. New balance: " + Money.format(a.getBalance()));
        }));
        withdraw.addActionListener(e -> Ui.guard(this, () -> {
            Account a = selectedFrom();
            a.withdraw(amount());
            done("Withdrawn. New balance: " + Money.format(a.getBalance()));
        }));
        transfer.addActionListener(e -> Ui.guard(this, () -> {
            Account from = selectedFrom();
            Account to = toBox.getSelected();
            if (to == null) throw new InvalidInputException("Select a destination account.");
            bank.transfer(from.getAccountNumber(), to.getAccountNumber(), amount());
            done("Transfer complete. New balance: " + Money.format(from.getBalance()));
        }));
        fromBox.addActionListener(e -> showInfo());

        add(form, BorderLayout.NORTH);
        refresh();
    }

    private Account selectedFrom() throws InvalidInputException {
        Account a = fromBox.getSelected();
        if (a == null) throw new InvalidInputException("Select an account first.");
        return a;
    }

    private double amount() throws InvalidInputException {
        return Validator.parseAmount("Amount", amountField.getText());
    }

    private void done(String message) {
        amountField.setText("");
        onChange.run();
        Ui.info(this, message);
    }

    private void showInfo() {
        Account a = fromBox.getSelected();
        infoLabel.setText(a == null ? " " : "Balance " + Money.format(a.getBalance())
                + " | Available " + Money.format(a.getAvailableFunds()));
    }

    @Override public void refresh() {
        fromBox.reload(bank.getAccounts());
        toBox.reload(bank.getAccounts());
        showInfo();
    }
}
