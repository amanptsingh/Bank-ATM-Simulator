package bank.threads;

import bank.model.Account;

/** Thread created by implementing the Runnable interface. */
public class DepositTask implements Runnable {
    private Account account;
    private double amount;
    private int times;

    public DepositTask(Account account, double amount, int times) {
        this.account = account;
        this.amount = amount;
        this.times = times;
    }

    public void run() {
        for (int i = 0; i < times; i++) {
            account.deposit(amount);   // deposit() is synchronized
        }
    }
}
