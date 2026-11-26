package bank.exceptions;

/**
 * Our own checked exception. It is thrown when the customer tries to
 * withdraw more money than the account allows.
 */
public class InsufficientFundsException extends Exception {
    private double shortfall;

    public InsufficientFundsException(double requested, double available) {
        super("Insufficient funds! Requested: Rs." + requested + ", Available: Rs." + available);
        this.shortfall = requested - available;
    }

    public double getShortfall() {
        return shortfall;
    }
}
