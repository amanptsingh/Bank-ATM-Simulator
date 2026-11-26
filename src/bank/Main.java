package bank;

import bank.exceptions.AccountNotFoundException;
import bank.exceptions.InsufficientFundsException;
import bank.exceptions.InvalidPinException;
import bank.gui.AwtLoginWindow;
import bank.io.FileHandler;
import bank.io.PipeDemo;
import bank.model.Account;
import bank.service.Bank;
import bank.service.CashDispenser;
import bank.threads.ThreadDemo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Bank / ATM Simulator - console version (also starts the GUI).
 * Run:  java -cp out bank.Main        (console menu)
 *       java -cp out bank.Main gui    (opens AWT/Swing windows directly)
 */
public class Main {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Bank bank = new Bank("Student National Bank");

    public static void main(String[] args) {
        // load old data from file
        try {
            bank.load();
            System.out.println("Loaded " + bank.getCount() + " account(s) from file.");
        } catch (IOException e) {
            System.out.println("Could not read data file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Data file is corrupted: " + e.getMessage());
        }

        if (args.length > 0 && args[0].equals("gui")) {
            new AwtLoginWindow(bank);
            return;   // GUI keeps running on its own
        }

        int choice = 0;
        do {
            System.out.println("\n==============================");
            System.out.println("  " + bank.getBankName());
            System.out.println("  " + bank.getInfo());
            System.out.println("==============================");
            System.out.println("1. Create new account");
            System.out.println("2. ATM login (console)");
            System.out.println("3. Open ATM GUI (AWT + Swing)");
            System.out.println("4. Multithreading demo");
            System.out.println("5. Pipe stream demo");
            System.out.println("6. View transaction log");
            System.out.println("7. Backup data and write summary");
            System.out.println("8. Show all accounts");
            System.out.println("9. Save and exit");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    new AwtLoginWindow(bank);
                    System.out.println("GUI opened. Close it and come back here to continue.");
                    break;
                case 4:
                    threadMenu();
                    break;
                case 5:
                    try {
                        PipeDemo.run(accountsArray(), bank.getCount());
                    } catch (IOException e) {
                        System.out.println("Pipe problem: " + e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println(FileHandler.readLog());
                    break;
                case 7:
                    backup();
                    break;
                case 8:
                    for (int i = 0; i < bank.getCount(); i++) {
                        System.out.println(bank.getAccountAt(i));
                    }
                    break;
                case 9:
                    saveAndExit();
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 9);
    }

    // ---------------- menu actions ----------------

    private static void createAccount() {
        try {
            String name = readLine("Enter name: ");
            int pin = readInt("Set a 4 digit PIN: ");
            String type = readLine("Account type (Savings/Current): ");
            double opening = readDouble("Opening balance: ");

            Account acc = bank.createAccount(name, pin, opening, type);
            System.out.println("Account created! Your account number is " + acc.getAccountNumber());
            FileHandler.writeLog("Account " + acc.getAccountNumber() + " created for " + name);
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create account: " + e.getMessage());
        }
    }

    private static void login() {
        try {
            int accNo = readInt("Account number: ");
            int pin = readInt("PIN: ");
            Account acc = bank.login(accNo, pin);
            System.out.println("Welcome, " + acc.getHolderName() + "!");
            atmSession(acc);
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidPinException e) {
            System.out.println("Login failed: " + e.getMessage());
        } finally {
            System.out.println("(Login attempt finished)");
        }
    }

    private static void atmSession(Account acc) {
        int choice = 0;
        do {
            System.out.println("\n--- ATM MENU ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check balance");
            System.out.println("4. Mini statement");
            System.out.println("5. Transfer money");
            System.out.println("6. Change PIN");
            System.out.println("7. Logout");
            choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        double dep = readDouble("Amount to deposit: ");
                        acc.deposit(dep);
                        System.out.println("Deposited Rs." + dep + ". New balance: Rs." + acc.getBalance());
                        FileHandler.writeLog("Acc " + acc.getAccountNumber() + " deposited Rs." + dep);
                        break;
                    case 2:
                        double wd = readDouble("Amount to withdraw: ");
                        if (!CashDispenser.isValidAmount(wd)) {
                            System.out.println("ATM gives only multiples of Rs.100.");
                            break;
                        }
                        acc.withdraw(wd);
                        System.out.println(CashDispenser.describe((int) wd));
                        System.out.println("Remaining balance: Rs." + acc.getBalance());
                        FileHandler.writeLog("Acc " + acc.getAccountNumber() + " withdrew Rs." + wd);
                        break;
                    case 3:
                        System.out.println("Current balance: Rs." + acc.getBalance());
                        break;
                    case 4:
                        Account.MiniStatement ms = acc.getMiniStatement(5);
                        System.out.println(ms.build());
                        break;
                    case 5:
                        int target = readInt("Target account number: ");
                        double amt = readDouble("Amount to transfer: ");
                        bank.transfer(acc.getAccountNumber(), target, amt);
                        System.out.println("Transferred Rs." + amt + " to account " + target);
                        FileHandler.writeLog("Transfer Rs." + amt + " from " + acc.getAccountNumber() + " to " + target);
                        break;
                    case 6:
                        int oldPin = readInt("Old PIN: ");
                        int newPin = readInt("New PIN: ");
                        acc.changePin(oldPin, newPin);
                        System.out.println("PIN changed successfully.");
                        break;
                    case 7:
                        System.out.println("Logged out. Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InsufficientFundsException e) {
                System.out.println(e.getMessage());
                System.out.println("You are short by Rs." + e.getShortfall());
            } catch (AccountNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (InvalidPinException e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        } while (choice != 7);
    }

    private static void threadMenu() {
        System.out.println("\n-- Thread life cycle --");
        ThreadDemo.showLifeCycle();

        System.out.println("\n-- Two threads, one counter --");
        ThreadDemo.counterDemo(false);
        ThreadDemo.counterDemo(true);

        if (bank.getCount() == 0) {
            System.out.println("\nCreate at least one account to see the deposit and interest demos.");
            return;
        }
        System.out.println("\n-- Two threads depositing in " + bank.getAccountAt(0).getHolderName() + "'s account --");
        ThreadDemo.depositDemo(bank.getAccountAt(0));

        System.out.println("\n-- Interest thread (3 months) --");
        ThreadDemo.interestDemo(bank);
    }

    private static void backup() {
        try {
            bank.save();
            FileHandler.backupData();
            FileHandler.writeSummary(accountsArray(), bank.getCount());
            System.out.println("Backup done. Summary file contents:");
            System.out.println(FileHandler.readSummary());
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private static void saveAndExit() {
        try {
            bank.save();
            System.out.println("Data saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // helper: copy accounts into an array for the IO classes
    private static Account[] accountsArray() {
        Account[] arr = new Account[bank.getCount()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bank.getAccountAt(i);
        }
        return arr;
    }

    // ---------------- input helpers (wrapper classes used here) ----------------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            String line = br.readLine();
            if (line == null) {          // input finished (Ctrl+D)
                saveAndExit();
                System.exit(0);
            }
            return line.trim();
        } catch (IOException e) {
            System.out.println("Input error: " + e.getMessage());
            return "";
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                Integer value = Integer.valueOf(text);   // wrapper class
                return value.intValue();
            } catch (NumberFormatException e) {
                System.out.println("Please type a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Double.parseDouble(text);         // wrapper class
            } catch (NumberFormatException e) {
                System.out.println("Please type a valid number.");
            }
        }
    }
}
