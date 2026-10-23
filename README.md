# Java Banking System (Swing GUI)

A GUI banking application built with Java Swing that demonstrates the Four Pillars of OOP.

## Run in GitHub Codespaces
1. Push this folder to a GitHub repository, then **Code → Codespaces → Create codespace**.
2. Wait for the container to build (it installs Java 21 and a browser-based desktop).
3. In the terminal run: `./run.sh`
4. Open the **Ports** tab, click the globe icon next to port **6080** (Desktop noVNC). Password: `vscode`.
5. The Banking System window appears inside that desktop.

Run the automated checks with `./test.sh`.
Run locally (JDK 17+): `./run.sh`

## System features (9)
1. Open account (Savings / Checking / Fixed Deposit)
2. View all accounts
3. Search accounts (number, name, type)
4. Deposit
5. Withdraw
6. Transfer between accounts
7. View transaction history
8. Month-end processing (interest / fees)
9. Close account and bank summary report

Data is saved automatically to `data/bank.dat`.

## Project structure
```
src/bank/
  Main.java
  exception/  BankException, InvalidInputException, InsufficientFundsException, AccountNotFoundException
  model/      Account (abstract), SavingsAccount, CheckingAccount, FixedDepositAccount,
              Transactable, InterestBearing, Customer, Transaction, AccountType
  service/    Bank, BankStorage
  util/       Validator, Money
  gui/        MainFrame, AccountsPanel, TransactionPanel, HistoryPanel, ReportPanel,
              AccountComboBox, Refreshable, Ui
  test/       BankTests
```

## OOP implementation (for your documentation and defense)

| Pillar | Where | Explanation |
|---|---|---|
| **Encapsulation** | `Account`, `Customer`, `Transaction` | All fields are `private`. `balance` has a getter but **no setter**; it only changes through `deposit`/`withdraw`/`adjustBalance`, which validate first. `Transaction` is immutable (`final` fields, no setters). `getHistory()` returns an unmodifiable list. |
| **Inheritance** | `Account` → `SavingsAccount`, `CheckingAccount`, `FixedDepositAccount`; `AccountComboBox extends JComboBox<Account>`; exceptions extend `BankException` | Children reuse the parent's account number, owner, balance, history, `deposit` and `withdraw`, and add only what differs. |
| **Polymorphism** | `getAvailableFunds()`, `applyMonthEnd()`, `getAccountType()` (overriding); `FixedDepositAccount.withdraw(...)` (overrides parent rule); `Bank.applyMonthEndToAll()`; `MainFrame` refreshing `Refreshable` screens | `Bank` calls `account.applyMonthEnd()` on an `Account` reference: Savings credits interest, Checking charges a fee, Fixed Deposit credits higher interest. `FixedDepositAccount` overrides `withdraw` to add a 2% penalty. |
| **Abstraction** | abstract class `Account`; interfaces `Transactable`, `InterestBearing`, `Refreshable` | `Account` cannot be instantiated and forces each subclass to define its own rules. `InterestBearing` is implemented only by Savings and Fixed Deposit. `Refreshable` lets every screen be redrawn without `MainFrame` knowing the concrete panel. |

### Account rules
| Type | Min. opening | Withdrawal rule | Month-end |
|---|---|---|---|
| Savings | PHP 500 | Must keep PHP 500 minimum balance | +3% p.a. interest (monthly) |
| Checking | PHP 1,000 | Overdraft up to PHP 5,000 | PHP 50 fee if balance < PHP 5,000 |
| Fixed Deposit | PHP 5,000 | 2% early-withdrawal penalty | +6% p.a. interest (monthly) |

## UML class diagram
GitHub renders this automatically. You can also paste it into https://mermaid.live to export an image for your documentation.

```mermaid
classDiagram
    direction TB
    class Transactable {
        <<interface>>
        +deposit(double) void
        +withdraw(double) void
        +getBalance() double
    }
    class InterestBearing {
        <<interface>>
        +getAnnualInterestRate() double
        +calculateMonthlyInterest() double
    }
    class Refreshable {
        <<interface>>
        +refresh() void
    }
    class Account {
        <<abstract>>
        -String accountNumber
        -Customer owner
        -double balance
        -List~Transaction~ history
        #Account(String, Customer, double)
        +getAccountNumber() String
        +getOwner() Customer
        +getBalance() double
        +getHistory() List~Transaction~
        +deposit(double) void
        +deposit(double, Type, String) void
        +withdraw(double) void
        +withdraw(double, Type, String) void
        #adjustBalance(double, Type, String) void
        +getAccountType()* String
        +getAvailableFunds()* double
        +applyMonthEnd()* String
    }
    class SavingsAccount {
        +ANNUAL_RATE$ double
        +MINIMUM_BALANCE$ double
        +getAccountType() String
        +getAvailableFunds() double
        +calculateMonthlyInterest() double
        +applyMonthEnd() String
    }
    class CheckingAccount {
        +OVERDRAFT_LIMIT$ double
        +MONTHLY_FEE$ double
        +getAccountType() String
        +getAvailableFunds() double
        +applyMonthEnd() String
    }
    class FixedDepositAccount {
        +ANNUAL_RATE$ double
        +PENALTY_RATE$ double
        +getAccountType() String
        +getAvailableFunds() double
        +withdraw(double, Type, String) void
        +calculateMonthlyInterest() double
        +applyMonthEnd() String
    }
    class Customer {
        -String name
        -String phone
        -String email
        +getName() String
        +getPhone() String
        +getEmail() String
        +setPhone(String) void
        +setEmail(String) void
    }
    class Transaction {
        -LocalDateTime timestamp
        -Type type
        -double amount
        -double balanceAfter
        -String note
        +getTimestamp() LocalDateTime
        +getType() Type
        +getAmount() double
        +getBalanceAfter() double
        +getNote() String
    }
    class AccountType {
        <<enumeration>>
        SAVINGS
        CHECKING
        FIXED_DEPOSIT
        +getMinimumOpeningDeposit() double
    }
    class Bank {
        -Map~String,Account~ accounts
        -int nextNumber
        +openAccount(AccountType, String, String, String, double) Account
        +getAccount(String) Account
        +getAccounts() Collection~Account~
        +search(String) List~Account~
        +closeAccount(String) void
        +transfer(String, String, double) void
        +applyMonthEndToAll() List~String~
        +getTotalDeposits() double
    }
    class BankStorage {
        -Path file
        +save(Bank) void
        +load() Bank
    }
    class Validator {
        <<utility>>
        +requireText(String, String)$ String
        +requireName(String)$ String
        +requirePhone(String)$ String
        +requireEmail(String)$ String
        +parseAmount(String, String)$ double
        +requirePositive(double)$ double
    }
    class BankException
    class InvalidInputException
    class InsufficientFundsException
    class AccountNotFoundException
    class MainFrame {
        -Bank bank
        -BankStorage storage
        -List~Refreshable~ screens
        -refreshAll() void
    }
    class AccountsPanel
    class TransactionPanel
    class HistoryPanel
    class ReportPanel
    class AccountComboBox

    Transactable <|.. Account
    Account <|-- SavingsAccount
    Account <|-- CheckingAccount
    Account <|-- FixedDepositAccount
    InterestBearing <|.. SavingsAccount
    InterestBearing <|.. FixedDepositAccount
    Account "1" *-- "1" Customer : owner
    Account "1" *-- "0..*" Transaction : history
    Bank "1" o-- "0..*" Account
    Bank ..> AccountType
    Bank ..> Validator
    BankStorage ..> Bank
    BankException <|-- InvalidInputException
    BankException <|-- InsufficientFundsException
    BankException <|-- AccountNotFoundException
    Refreshable <|.. AccountsPanel
    Refreshable <|.. TransactionPanel
    Refreshable <|.. HistoryPanel
    Refreshable <|.. ReportPanel
    JComboBox <|-- AccountComboBox
    MainFrame "1" o-- "1" Bank
    MainFrame "1" o-- "1..*" Refreshable
    AccountsPanel ..> Bank
    TransactionPanel ..> Bank
    HistoryPanel ..> Bank
    ReportPanel ..> Bank
```

## Input validation and error handling
| Check | Where |
|---|---|
| Required fields (name, contact, email, amount) | `Validator.requireText` |
| Name / phone / email format | `Validator.requireName / requirePhone / requireEmail` |
| Numeric amount, > 0, max, 2 decimals | `Validator.parseAmount / requirePositive` |
| Minimum opening deposit per type | `Bank.openAccount` |
| Insufficient funds / minimum balance / overdraft / penalty | `Account.withdraw`, `FixedDepositAccount.withdraw` |
| Transfer to same account, unknown account | `Bank.transfer`, `Bank.getAccount` |
| Cannot close account with balance | `Bank.closeAccount` |
| All errors shown as dialogs, never a crash | `Ui.guard` |

## Testing evidence
`./test.sh` runs 20 automated checks (T01-T20) covering opening accounts, validation, deposits, withdrawals,
overdraft, penalties, transfers, search, closing, month-end polymorphism, and history. Last result: **20 passed, 0 failed**.
For your documentation, also take GUI screenshots of: each tab, a successful transaction, and at least two error dialogs
(e.g. empty name, withdrawing too much) and paste them in the Testing section.

## Documentation checklist (Section XI of your project sheet)
Title: *Java Banking System* · Description · Problem statement (manual banking is slow and error-prone) · Objectives ·
System features (list above) · OOP implementation (table above) · UML (above) · GUI screenshots (you take them) · Testing · Conclusion.
