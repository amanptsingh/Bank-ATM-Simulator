package bank.service;

import bank.exceptions.AccountNotFoundException;
import bank.exceptions.InsufficientFundsException;
import bank.exceptions.InvalidPinException;
import bank.io.FileHandler;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;
import java.io.IOException;

/**
 * Main class that holds all accounts.
 * Accounts are kept in a normal array (max 100 customers).
 */
public class Bank {

    /** Static nested class: does not need a Bank object to be created. */
    public static class BankInfo {
        private String ifscCode;
        private String branch;

        public BankInfo(String ifscCode, String branch) {
            this.ifscCode = ifscCode;
            this.branch = branch;
        }

        public String toString() {
            return "IFSC: " + ifscCode + ", Branch: " + branch;
        }
    }

    private String bankName;
    private BankInfo info;
    private Account[] accounts;
    private int count;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.info = new BankInfo("SNB0001234", "Main Branch");
        this.accounts = new Account[100];
        this.count = 0;
    }

    public Account createAccount(String name, int pin, double opening, String type) {
        if (count == accounts.length) {
            throw new IllegalArgumentException("Bank is full, cannot add more accounts.");
        }
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (pin < 1000 || pin > 9999) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }

        Account acc;
        if (type.equalsIgnoreCase("Savings")) {
            if (opening < SavingsAccount.MIN_BALANCE) {
                throw new IllegalArgumentException("Savings account needs at least Rs."
                        + SavingsAccount.MIN_BALANCE + " to open.");
            }
            acc = new SavingsAccount(name, pin, opening);
        } else {
            if (opening < 0) {
                throw new IllegalArgumentException("Opening balance cannot be negative.");
            }
            acc = new CurrentAccount(name, pin, opening);   // polymorphism: parent reference
        }

        accounts[count] = acc;
        count++;
        return acc;
    }

    public Account findAccount(int accNo) throws AccountNotFoundException {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber() == accNo) {
                return accounts[i];
            }
        }
        throw new AccountNotFoundException(accNo);
    }

    public Account login(int accNo, int pin) throws AccountNotFoundException, InvalidPinException {
        Account acc = findAccount(accNo);

        if (acc.isLocked()) {
            throw new InvalidPinException("This account is locked after 3 wrong attempts.");
        }
        if (!acc.checkPin(pin)) {
            acc.registerFailedAttempt();
            throw new InvalidPinException("Wrong PIN entered.");
        }
        acc.resetAttempts();
        return acc;
    }

    public synchronized void transfer(int fromNo, int toNo, double amount)
            throws AccountNotFoundException, InsufficientFundsException {
        if (fromNo == toNo) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        Account from = findAccount(fromNo);
        Account to = findAccount(toNo);
        from.withdraw(amount);
        to.deposit(amount);
    }

    // ---- saving / loading ----
    public void save() throws IOException {
        FileHandler.saveAccounts(accounts, count);
    }

    public void load() throws IOException, ClassNotFoundException {
        count = FileHandler.loadAccounts(accounts);
        // set next account number so new accounts do not clash with old ones
        int max = 1000;
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber() > max) {
                max = accounts[i].getAccountNumber();
            }
        }
        Account.setNextAccountNumber(max + 1);
    }

    // ---- getters ----
    public int getCount() {
        return count;
    }

    public Account getAccountAt(int index) {
        return accounts[index];
    }

    public String getBankName() {
        return bankName;
    }

    public String getInfo() {
        return info.toString();
    }
}
