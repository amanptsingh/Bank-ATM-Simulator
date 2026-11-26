package bank.model;

/** Savings account: needs a minimum balance and earns interest. */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double INTEREST_RATE = 4.0;   // percent per year
    public static final double MIN_BALANCE = 500;

    public SavingsAccount(String holderName, int pin, double openingBalance) {
        super(holderName, pin, openingBalance);   // call parent constructor
    }

    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    public String getAccountType() {
        return "Savings";
    }

    // adds one month of interest
    public synchronized void addInterest() {
        double interest = balance * INTEREST_RATE / 100 / 12;
        interest = Math.round(interest * 100) / 100.0;   // round to 2 decimals
        balance += interest;
        recordTransaction("INTEREST", interest);
    }

    public String toString() {
        return super.toString() + " | Rate: " + INTEREST_RATE + "%";
    }
}
