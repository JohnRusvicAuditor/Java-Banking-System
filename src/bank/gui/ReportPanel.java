package bank.gui;

import bank.model.Account;
import bank.service.Bank;
import bank.util.Money;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;

/** Features: bank summary report and month-end processing (interest / fees). */
class ReportPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final Bank bank;
    private final Runnable onChange;
    private final JTextArea summary = new JTextArea();
    private final JTextArea log = new JTextArea();

    ReportPanel(Bank bank, Runnable onChange) {
        this.bank = bank;
        this.onChange = onChange;
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (JTextArea area : new JTextArea[]{summary, log}) {
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        }
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Bank Summary"));
        left.add(new JScrollPane(summary));

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createTitledBorder("Month-End Processing"));
        JButton run = new JButton("Apply Interest / Fees to All Accounts");
        run.addActionListener(e -> Ui.guard(this, () -> {
            int ok = JOptionPane.showConfirmDialog(this, "Apply month-end processing to every account?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            StringBuilder sb = new StringBuilder();
            for (String line : bank.applyMonthEndToAll()) sb.append(line).append('\n');
            log.setText(sb.length() == 0 ? "No accounts to process." : sb.toString());
            onChange.run();
        }));
        right.add(run, BorderLayout.NORTH);
        right.add(new JScrollPane(log), BorderLayout.CENTER);

        add(left); add(right);
        refresh();
    }

    @Override public void refresh() {
        Map<String, Integer> counts = new TreeMap<>();
        Map<String, Double> totals = new TreeMap<>();
        for (Account a : bank.getAccounts()) {
            counts.merge(a.getAccountType(), 1, Integer::sum);
            totals.merge(a.getAccountType(), a.getBalance(), Double::sum);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Total accounts : ").append(bank.getAccounts().size()).append('\n');
        sb.append("Total deposits : ").append(Money.format(bank.getTotalDeposits())).append("\n\n");
        for (String type : counts.keySet()) {
            sb.append(String.format("%-14s %3d acct(s)  %s%n", type, counts.get(type),
                    Money.format(totals.get(type))));
        }
        summary.setText(sb.toString());
    }
}
