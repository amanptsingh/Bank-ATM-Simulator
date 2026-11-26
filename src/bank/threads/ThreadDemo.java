package bank.threads;

import bank.model.Account;
import bank.service.Bank;

/**
 * Small demos for the multi-threading unit:
 * 1) thread life cycle
 * 2) a problem when threads share data without synchronization
 * 3) synchronized deposit
 * 4) the interest thread
 */
public class ThreadDemo {

    // a simple counter shared by two threads
    static class Counter {
        private int value = 0;

        public void incrementUnsafe() {
            value++;                    // not synchronized -> updates can get lost
        }

        public synchronized void incrementSafe() {
            value++;                    // synchronized -> only one thread at a time
        }

        public int getValue() {
            return value;
        }
    }

    static class CounterTask implements Runnable {
        private Counter counter;
        private boolean safe;

        public CounterTask(Counter counter, boolean safe) {
            this.counter = counter;
            this.safe = safe;
        }

        public void run() {
            for (int i = 0; i < 100000; i++) {
                if (safe) {
                    counter.incrementSafe();
                } else {
                    counter.incrementUnsafe();
                }
            }
        }
    }

    // ---- 1) life cycle ----
    public static void showLifeCycle() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            }
        });

        try {
            System.out.println("After creating  : " + t.getState());   // NEW
            t.start();
            System.out.println("After start()   : " + t.getState());   // RUNNABLE
            Thread.sleep(100);
            System.out.println("While sleeping  : " + t.getState());   // TIMED_WAITING
            t.join();
            System.out.println("After finishing : " + t.getState());   // TERMINATED
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }
    }

    // ---- 2) shared counter with and without synchronization ----
    public static void counterDemo(boolean safe) {
        Counter c = new Counter();
        Thread t1 = new Thread(new CounterTask(c, safe));
        Thread t2 = new Thread(new CounterTask(c, safe));
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        System.out.println((safe ? "Synchronized" : "Unsynchronized")
                + " result = " + c.getValue() + " (expected 200000)");
    }

    // ---- 3) two threads depositing in the same account ----
    public static void depositDemo(Account acc) {
        double before = acc.getBalance();
        Thread t1 = new Thread(new DepositTask(acc, 10, 100), "Depositor-1");
        Thread t2 = new Thread(new DepositTask(acc, 10, 100), "Depositor-2");
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        System.out.println("Balance before: Rs." + before);
        System.out.println("Balance after : Rs." + acc.getBalance() + " (expected Rs." + (before + 2000) + ")");
    }

    // ---- 4) interest thread ----
    public static void interestDemo(Bank bank) {
        InterestThread it = new InterestThread(bank, 3);
        it.start();
        try {
            it.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }
}
