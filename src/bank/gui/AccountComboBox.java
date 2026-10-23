package bank.gui;

import bank.model.Account;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/** INHERITANCE: a JComboBox specialised to hold Accounts and keep its selection on reload. */
class AccountComboBox extends JComboBox<Account> {
    private static final long serialVersionUID = 1L;

    void reload(Collection<Account> accounts) {
        Account old = getSelected();
        setModel(new DefaultComboBoxModel<>(accounts.toArray(new Account[0])));
        if (old != null) {
            for (Account a : accounts) {
                if (a.getAccountNumber().equals(old.getAccountNumber())) { setSelectedItem(a); break; }
            }
        }
    }

    Account getSelected() { return (Account) getSelectedItem(); }
}
