package bank.test;

import bank.exception.*;
import bank.model.*;
import bank.service.Bank;
import bank.util.Validator;

/** Plain-Java test runner (no libraries needed). Run with ./test.sh */
public class BankTests {
    private static int passed = 0, failed = 0;

    interface Body { void run() throws Exception; }

    static void test(String name, Body body) {
        try { body.run(); passed++; System.out.println("PASS  " + name); }
        catch (Throwable t) { failed++; System.out.println("FAIL  " + name + " -> " + t); }
    }

    static void check(boolean cond, String msg) { if (!cond) throw new AssertionError(msg); }

    static <E extends Exception> void expect(Class<E> type, Body body) {
        try { body.run(); }
        catch (Exception e) { if (type.isInstance(e)) return; throw new AssertionError("Wrong exception: " + e); }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    static Bank bankWith(AccountType t, double amt) throws Exception {
        Bank b = new Bank();
        b.openAccount(t, "Juan Dela Cruz", "09171234567", "juan@mail.com", amt);
        return b;
    }

    public static void main(String[] args) {
        test("T01 Open savings account", () -> {
            Bank b = bankWith(AccountType.SAVINGS, 1000);
            check(b.getAccount("ACC-1001").getBalance() == 1000, "balance");
        });
        test("T02 Opening deposit below minimum rejected", () ->
            expect(InvalidInputException.class, () -> bankWith(AccountType.FIXED_DEPOSIT, 100)));
        test("T03 Blank name rejected", () ->
            expect(InvalidInputException.class, () -> new Bank().openAccount(AccountType.SAVINGS, " ", "09171234567", "a@b.com", 1000)));
        test("T04 Invalid email rejected", () ->
            expect(InvalidInputException.class, () -> new Bank().openAccount(AccountType.SAVINGS, "Ana", "09171234567", "bad-email", 1000)));
        test("T05 Invalid phone rejected", () ->
            expect(InvalidInputException.class, () -> new Bank().openAccount(AccountType.SAVINGS, "Ana", "abc", "a@b.com", 1000)));
        test("T06 Non-numeric amount rejected", () ->
            expect(InvalidInputException.class, () -> Validator.parseAmount("Amount", "12abc")));
        test("T07 Negative / zero amount rejected", () -> {
            expect(InvalidInputException.class, () -> Validator.parseAmount("Amount", "-5"));
            expect(InvalidInputException.class, () -> Validator.parseAmount("Amount", "0"));
        });
        test("T08 More than 2 decimals rejected", () ->
            expect(InvalidInputException.class, () -> Validator.parseAmount("Amount", "10.555")));
        test("T09 Deposit increases balance", () -> {
            Bank b = bankWith(AccountType.SAVINGS, 1000);
            b.getAccount("ACC-1001").deposit(250.50);
            check(b.getAccount("ACC-1001").getBalance() == 1250.50, "balance");
        });
        test("T10 Savings keeps minimum balance", () -> {
            Account a = bankWith(AccountType.SAVINGS, 1000).getAccount("ACC-1001");
            a.withdraw(500);
            expect(InsufficientFundsException.class, () -> a.withdraw(1));
        });
        test("T11 Checking allows overdraft up to limit", () -> {
            Account a = bankWith(AccountType.CHECKING, 1000).getAccount("ACC-1001");
            a.withdraw(6000);
            check(a.getBalance() == -5000, "balance");
            expect(InsufficientFundsException.class, () -> a.withdraw(1));
        });
        test("T12 Fixed deposit withdrawal charges 2% penalty", () -> {
            Account a = bankWith(AccountType.FIXED_DEPOSIT, 5000).getAccount("ACC-1001");
            a.withdraw(1000);
            check(a.getBalance() == 3980, "balance was " + a.getBalance());
        });
        test("T13 Transfer moves money between accounts", () -> {
            Bank b = bankWith(AccountType.CHECKING, 2000);
            b.openAccount(AccountType.SAVINGS, "Maria Santos", "09181234567", "maria@mail.com", 1000);
            b.transfer("ACC-1001", "ACC-1002", 500);
            check(b.getAccount("ACC-1001").getBalance() == 1500, "from");
            check(b.getAccount("ACC-1002").getBalance() == 1500, "to");
        });
        test("T14 Transfer to same account rejected", () ->
            expect(InvalidInputException.class, () -> bankWith(AccountType.SAVINGS, 1000).transfer("ACC-1001", "ACC-1001", 10)));
        test("T15 Unknown account rejected", () ->
            expect(AccountNotFoundException.class, () -> new Bank().getAccount("ACC-9999")));
        test("T16 Cannot close account with balance", () ->
            expect(InvalidInputException.class, () -> bankWith(AccountType.SAVINGS, 1000).closeAccount("ACC-1001")));
        test("T17 Can close account with zero balance", () -> {
            Bank b = bankWith(AccountType.CHECKING, 1000);
            b.getAccount("ACC-1001").withdraw(1000);
            b.closeAccount("ACC-1001");
            check(b.getAccounts().isEmpty(), "still exists");
        });
        test("T18 Search by name (case-insensitive)", () -> {
            Bank b = bankWith(AccountType.SAVINGS, 1000);
            check(b.search("juan").size() == 1 && b.search("zzz").isEmpty(), "search");
        });
        test("T19 Polymorphic month-end: interest vs fee", () -> {
            Bank b = bankWith(AccountType.SAVINGS, 12000);           // 12000*3%/12 = 30
            b.openAccount(AccountType.CHECKING, "Maria Santos", "09181234567", "m@mail.com", 1000); // fee 50
            b.applyMonthEndToAll();
            check(b.getAccount("ACC-1001").getBalance() == 12030, "savings");
            check(b.getAccount("ACC-1002").getBalance() == 950, "checking");
        });
        test("T20 History records every change", () -> {
            Account a = bankWith(AccountType.SAVINGS, 1000).getAccount("ACC-1001");
            a.deposit(100); a.withdraw(50);
            check(a.getHistory().size() == 3, "size " + a.getHistory().size());
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }
}
