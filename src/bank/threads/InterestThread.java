package bank.threads;

import bank.model.Account;
import bank.model.SavingsAccount;
import bank.service.Bank;

/**
 * Thread created by extending the Thread class.
 * Adds monthly interest to every savings account a few times.
 */
public class InterestThread extends Thread {
    private Bank bank;
    private int months;

    public InterestThread(Bank bank, int months) {
        super("InterestThread");
        this.bank = bank;
        this.months = months;
    }

    public void run() {
        for (int m = 1; m <= months; m++) {
            try {
                Thread.sleep(1000);   // wait 1 second = 1 "month"
            } catch (InterruptedException e) {
                System.out.println("Interest thread was interrupted!");
                return;
            }
            for (int i = 0; i < bank.getCount(); i++) {
                Account a = bank.getAccountAt(i);
                if (a instanceof SavingsAccount) {
                    ((SavingsAccount) a).addInterest();   // type casting
                }
            }
            System.out.println("[" + getName() + "] Month " + m + " interest added.");
        }
    }
}
