package bank.gui;

import bank.model.Account;
import bank.service.Bank;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** AWT window to open a new bank account. */
public class CreateAccountFrame extends Frame implements ActionListener {
    private Bank bank;
    private TextField nameField;
    private TextField pinField;
    private TextField balanceField;
    private Choice typeChoice;
    private Label statusLabel;
    private Button createBtn;
    private Button closeBtn;

    public CreateAccountFrame(Bank bank) {
        super("Open New Account");
        this.bank = bank;

        setLayout(new GridLayout(6, 2, 8, 8));

        add(new Label("Name:"));
        nameField = new TextField();
        add(nameField);

        add(new Label("4-digit PIN:"));
        pinField = new TextField();
        pinField.setEchoChar('*');
        add(pinField);

        add(new Label("Account Type:"));
        typeChoice = new Choice();
        typeChoice.add("Savings");
        typeChoice.add("Current");
        add(typeChoice);

        add(new Label("Opening Balance:"));
        balanceField = new TextField();
        add(balanceField);

        createBtn = new Button("Create");
        closeBtn = new Button("Close");
        add(createBtn);
        add(closeBtn);

        statusLabel = new Label("");
        add(statusLabel);

        createBtn.addActionListener(this);
        closeBtn.addActionListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(380, 280);
        setLocation(350, 220);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            dispose();
            return;
        }

        try {
            String name = nameField.getText().trim();
            int pin = Integer.parseInt(pinField.getText().trim());
            double opening = Double.parseDouble(balanceField.getText().trim());
            String type = typeChoice.getSelectedItem();

            Account acc = bank.createAccount(name, pin, opening, type);
            statusLabel.setText("Done! Acc No = " + acc.getAccountNumber());
        } catch (NumberFormatException ex) {
            statusLabel.setText("PIN / balance must be numbers");
        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }
}
