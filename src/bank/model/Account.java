package bank.model;

import bank.exceptions.InsufficientFundsException;
import bank.exceptions.InvalidPinException;
import java.io.Serializable;

/**
 * Abstract parent class of all account types.
 * Shows: abstract class, interface, static members, access control,
 * this keyword, inner class and serialization.
 */
public abstract class Account implements Transactable, Serializable {
    private static final long serialVersionUID = 1L;

    // static variables belong to the class, not to any object
    private static int nextAccountNumber = 1001;
    private static int sessionAccounts = 0;

    // private -> only this class
    private int accountNumber;
    private String holderName;
    private int pin;
    private int failedAttempts;
    private Transaction[] history;
    private int txnCount;

    // protected -> this class, child classes and same package
    protected double balance;

    public Account(String holderName, int pin, double openingBalance) {
        this.accountNumber = nextAccountNumber;
        nextAccountNumber++;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = openingBalance;
        this.history = new Transaction[50];
        this.txnCount = 0;
        this.failedAttempts = 0;
        sessionAccounts++;
        recordTransaction("OPENING", openingBalance);
    }

    // ---- abstract methods, every child must write its own version ----
    public abstract double getMinimumBalance();

    public abstract String getAccountType();

    // ---- static methods ----
    public static int getSessionAccounts() {
        return sessionAccounts;
    }

    public static void setNextAccountNumber(int n) {
        nextAccountNumber = n;
    }

    // ---- Transactable methods ----
    // synchronized so that two threads cannot change balance at the same time
    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }
        if (amount > MAX_LIMIT) {
            throw new IllegalArgumentException("Cannot deposit more than Rs." + MAX_LIMIT + " at once");
        }
        balance = balance + amount;
        recordTransaction("DEPOSIT", amount);
    }

    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than 0");
        }
        if (amount > MAX_LIMIT) {
            throw new IllegalArgumentException("Cannot withdraw more than Rs." + MAX_LIMIT + " at once");
        }
        double available = balance - getMinimumBalance();
        if (amount > available) {
            throw new InsufficientFundsException(amount, available);
        }
        balance -= amount;
        recordTransaction("WITHDRAW", amount);
    }

    // ---- PIN handling ----
    public boolean checkPin(int enteredPin) {
        return this.pin == enteredPin;
    }

    public void changePin(int oldPin, int newPin) throws InvalidPinException {
        if (!checkPin(oldPin)) {
            throw new InvalidPinException("Old PIN is incorrect.");
        }
        if (newPin < 1000 || newPin > 9999) {
            throw new InvalidPinException("New PIN must be exactly 4 digits.");
        }
        this.pin = newPin;
    }

    public void registerFailedAttempt() {
        failedAttempts++;
    }

    public void resetAttempts() {
        failedAttempts = 0;
    }

    public boolean isLocked() {
        return failedAttempts >= 3;
    }

    // ---- history ----
    protected void recordTransaction(String type, double amount) {
        Transaction t = new Transaction(type, amount, balance);
        if (txnCount == history.length) {
            // array is full, shift everything left and drop the oldest one
            for (int i = 1; i < history.length; i++) {
                history[i - 1] = history[i];
            }
            txnCount--;
        }
        history[txnCount] = t;
        txnCount++;
    }

    /**
     * Inner class (non-static). It can directly use the private
     * variables of Account because it lives inside Account.
     */
    public class MiniStatement {
        private int howMany;

        public MiniStatement(int howMany) {
            this.howMany = howMany;
        }

        public String build() {
            String result = "--- Mini Statement (Acc " + accountNumber + ") ---\n";
            int start = txnCount - howMany;
            if (start < 0) {
                start = 0;
            }
            for (int i = start; i < txnCount; i++) {
                result = result + history[i] + "\n";
            }
            result = result + "Balance: Rs." + balance;
            return result;
        }
    }

    public MiniStatement getMiniStatement(int howMany) {
        return new MiniStatement(howMany);
    }

    // ---- getters ----
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public String toString() {
        return "Acc No: " + accountNumber + " | " + holderName + " | " + getAccountType()
                + " | Balance: Rs." + balance;
    }
}
