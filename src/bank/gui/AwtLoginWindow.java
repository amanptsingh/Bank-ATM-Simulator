package bank.gui;

import bank.exceptions.AccountNotFoundException;
import bank.exceptions.InvalidPinException;
import bank.model.Account;
import bank.service.Bank;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Login screen made using AWT components.
 * Layouts used: BorderLayout, GridLayout, FlowLayout.
 */
public class AwtLoginWindow extends Frame implements ActionListener {
    private Bank bank;
    private TextField accField;
    private TextField pinField;
    private Label messageLabel;
    private Button loginBtn;
    private Button createBtn;
    private Button exitBtn;

    public AwtLoginWindow(Bank bank) {
        super("ATM Login - " + bank.getBankName());
        this.bank = bank;

        setLayout(new BorderLayout(10, 10));

        Label title = new Label("WELCOME TO THE ATM", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // centre: 2 rows x 2 columns
        Panel form = new Panel(new GridLayout(2, 2, 10, 10));
        form.add(new Label("Account Number:"));
        accField = new TextField(15);
        form.add(accField);
        form.add(new Label("PIN:"));
        pinField = new TextField(15);
        pinField.setEchoChar('*');
        form.add(pinField);
        add(form, BorderLayout.CENTER);

        // bottom: message + buttons
        Panel bottom = new Panel(new GridLayout(2, 1));
        messageLabel = new Label("Please login to continue", Label.CENTER);
        bottom.add(messageLabel);

        Panel buttons = new Panel(new FlowLayout());
        loginBtn = new Button("Login");
        createBtn = new Button("New Account");
        exitBtn = new Button("Exit");
        buttons.add(loginBtn);
        buttons.add(createBtn);
        buttons.add(exitBtn);
        bottom.add(buttons);
        add(bottom, BorderLayout.SOUTH);

        // register listeners (event handling)
        loginBtn.addActionListener(this);
        createBtn.addActionListener(this);
        exitBtn.addActionListener(this);

        // anonymous inner class to handle the window close button
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setSize(400, 230);
        setLocation(300, 200);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == loginBtn) {
            doLogin();
        } else if (source == createBtn) {
            new CreateAccountFrame(bank);
        } else if (source == exitBtn) {
            exitApplication();
        }
    }

    private void doLogin() {
        try {
            int accNo = Integer.parseInt(accField.getText().trim());
            int pin = Integer.parseInt(pinField.getText().trim());

            Account acc = bank.login(accNo, pin);
            messageLabel.setText("Login successful");
            setVisible(false);
            new ATMFrame(bank, acc, this);
        } catch (NumberFormatException ex) {
            messageLabel.setText("Account number and PIN must be numbers!");
        } catch (AccountNotFoundException ex) {
            messageLabel.setText(ex.getMessage());
        } catch (InvalidPinException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }

    // called by ATMFrame after logout
    public void reset() {
        accField.setText("");
        pinField.setText("");
        messageLabel.setText("Logged out. Please login again.");
    }

    private void exitApplication() {
        try {
            bank.save();
        } catch (IOException ex) {
            System.out.println("Could not save: " + ex.getMessage());
        }
        dispose();
        System.exit(0);
    }
}
