package bank.exceptions;

/** Thrown when an account number does not exist in the bank. */
public class AccountNotFoundException extends Exception {
    private int accountNumber;

    public AccountNotFoundException(int accountNumber) {
        super("Account number " + accountNumber + " was not found.");
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}
