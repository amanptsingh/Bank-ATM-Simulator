package bank.gui;

import bank.exceptions.AccountNotFoundException;
import bank.exceptions.InsufficientFundsException;
import bank.io.FileHandler;
import bank.model.Account;
import bank.service.Bank;
import bank.service.CashDispenser;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Main ATM screen made with Swing components. */
public class ATMFrame extends JFrame implements ActionListener {
    private Bank bank;
    private Account account;
    private AwtLoginWindow loginWindow;

    private JLabel welcomeLabel;
    private JTextArea display;
    private JTextField amountField;
    private JButton depositBtn, withdrawBtn, balanceBtn, statementBtn, transferBtn, logoutBtn;

    public ATMFrame(Bank bank, Account account, AwtLoginWindow loginWindow) {
        super("ATM - " + bank.getBankName());
        this.bank = bank;
        this.account = account;
        this.loginWindow = loginWindow;

        setLayout(new BorderLayout(5, 5));

        // ---- top ----
        welcomeLabel = new JLabel("Welcome, " + account.getHolderName()
                + "  (Acc " + account.getAccountNumber() + ", " + account.getAccountType() + ")",
                JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        add(welcomeLabel, BorderLayout.NORTH);

        // ---- centre: display screen ----
        display = new JTextArea(10, 35);
        display.setEditable(false);
        display.setFont(new Font("Monospaced", Font.PLAIN, 12));
        display.setText("Choose an option below.\nEnter amount first for deposit / withdraw / transfer.");
        add(new JScrollPane(display), BorderLayout.CENTER);

        // ---- bottom: amount box and buttons ----
        JPanel south = new JPanel(new BorderLayout());

        JPanel amountPanel = new JPanel(new FlowLayout());
        amountPanel.add(new JLabel("Amount (Rs.):"));
        amountField = new JTextField(10);
        amountPanel.add(amountField);
        south.add(amountPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        depositBtn = new JButton("Deposit");
        withdrawBtn = new JButton("Withdraw");
        balanceBtn = new JButton("Balance");
        statementBtn = new JButton("Mini Statement");
        transferBtn = new JButton("Transfer");
        logoutBtn = new JButton("Logout");

        JButton[] all = {depositBtn, withdrawBtn, balanceBtn, statementBtn, transferBtn, logoutBtn};
        for (int i = 0; i < all.length; i++) {
            all[i].addActionListener(this);
            buttonPanel.add(all[i]);
        }
        south.add(buttonPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // closing the window is treated as logout
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        pack();
        setLocation(300, 150);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        try {
            if (src == depositBtn) {
                double amt = readAmount();
                account.deposit(amt);
                display.setText("Rs." + amt + " deposited.\nNew balance: Rs." + account.getBalance());
                FileHandler.writeLog("GUI: Acc " + account.getAccountNumber() + " deposited Rs." + amt);

            } else if (src == withdrawBtn) {
                double amt = readAmount();
                if (!CashDispenser.isValidAmount(amt)) {
                    display.setText("ATM gives only multiples of Rs.100.");
                } else {
                    account.withdraw(amt);
                    display.setText(CashDispenser.describe((int) amt)
                            + "\nRemaining balance: Rs." + account.getBalance());
                    FileHandler.writeLog("GUI: Acc " + account.getAccountNumber() + " withdrew Rs." + amt);
                }

            } else if (src == balanceBtn) {
                display.setText("Current balance: Rs." + account.getBalance());

            } else if (src == statementBtn) {
                display.setText(account.getMiniStatement(5).build());

            } else if (src == transferBtn) {
                double amt = readAmount();
                String input = JOptionPane.showInputDialog(this, "Enter target account number:");
                if (input != null) {
                    int target = Integer.parseInt(input.trim());
                    bank.transfer(account.getAccountNumber(), target, amt);
                    display.setText("Rs." + amt + " sent to account " + target
                            + "\nRemaining balance: Rs." + account.getBalance());
                    FileHandler.writeLog("GUI: Transfer Rs." + amt + " from "
                            + account.getAccountNumber() + " to " + target);
                }

            } else if (src == logoutBtn) {
                logout();
            }
        } catch (NumberFormatException ex) {
            display.setText("Please enter a valid number!");
        } catch (InsufficientFundsException ex) {
            display.setText(ex.getMessage());
        } catch (AccountNotFoundException ex) {
            display.setText(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            display.setText("Error: " + ex.getMessage());
        } finally {
            amountField.setText("");
        }
    }

    private double readAmount() {
        String text = amountField.getText().trim();
        return Double.parseDouble(text);   // throws NumberFormatException if wrong
    }

    private void logout() {
        try {
            bank.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save data: " + ex.getMessage());
        }
        dispose();
        loginWindow.reset();
        loginWindow.setVisible(true);
    }
}
