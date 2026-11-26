package bank.model;

import java.io.Serializable;
import java.util.Date;

/** Stores one entry of the account history. */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private double amount;
    private double balanceAfter;
    private String date;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.date = new Date().toString();
    }

    // constructor overloading: balance not known
    public Transaction(String type, double amount) {
        this(type, amount, 0);
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String toString() {
        return date + " | " + type + " | Rs." + amount + " | Bal: Rs." + balanceAfter;
    }
}
