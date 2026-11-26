package bank.applet;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Swing applet (JApplet) - simple interest calculator.
 * The bank name and the interest rate come from the HTML page as parameters.
 */
public class InterestApplet extends JApplet {
    private String bankName;
    private double rate;
    private JTextField principalField;
    private JTextField yearsField;
    private JLabel resultLabel;

    public void init() {
        bankName = getParameter("bankName");
        if (bankName == null) {
            bankName = "Student National Bank";
        }

        String rateText = getParameter("rate");
        try {
            rate = Double.parseDouble(rateText);
        } catch (NumberFormatException e) {
            rate = 4.0;            // default if parameter is missing or wrong
        } catch (NullPointerException e) {
            rate = 4.0;
        }

        setLayout(new BorderLayout(5, 5));
        add(new JLabel(bankName + " - Interest Calculator (" + rate + "%)", JLabel.CENTER),
                BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Principal (Rs.):"));
        principalField = new JTextField();
        form.add(principalField);
        form.add(new JLabel("Time (years):"));
        yearsField = new JTextField();
        form.add(yearsField);
        add(form, BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(2, 1));
        JButton calcBtn = new JButton("Calculate");
        resultLabel = new JLabel("Result will be shown here", JLabel.CENTER);
        south.add(calcBtn);
        south.add(resultLabel);
        add(south, BorderLayout.SOUTH);

        // inner class as event listener
        calcBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
    }

    private void calculate() {
        try {
            double p = Double.parseDouble(principalField.getText().trim());
            double t = Double.parseDouble(yearsField.getText().trim());
            double interest = p * rate * t / 100;
            resultLabel.setText("Interest = Rs." + interest + "   Total = Rs." + (p + interest));
        } catch (NumberFormatException ex) {
            resultLabel.setText("Please enter numbers only!");
        }
    }

    public void start() {
        showStatus("Applet started");
    }

    public void stop() {
        System.out.println("Applet stopped");
    }

    public void destroy() {
        System.out.println("Applet destroyed");
    }
}
