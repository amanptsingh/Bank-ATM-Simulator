package bank.model;

/** Current account: no interest, but allows an overdraft. */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final double OVERDRAFT_LIMIT = 5000;

    public CurrentAccount(String holderName, int pin, double openingBalance) {
        super(holderName, pin, openingBalance);
    }

    // balance is allowed to go down to -5000
    public double getMinimumBalance() {
        return -OVERDRAFT_LIMIT;
    }

    public String getAccountType() {
        return "Current";
    }

    public String toString() {
        return super.toString() + " | Overdraft: Rs." + OVERDRAFT_LIMIT;
    }
}
