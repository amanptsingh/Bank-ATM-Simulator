package bank.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Simple Applet (AWT) to show the applet life cycle:
 * init() -> start() -> paint() -> stop() -> destroy()
 */
public class WelcomeApplet extends Applet {
    private String bankName;

    public void init() {
        bankName = getParameter("bankName");   // parameter passed from HTML
        if (bankName == null) {
            bankName = "Student National Bank";
        }
        setBackground(Color.WHITE);
        System.out.println("init() called");
    }

    public void start() {
        System.out.println("start() called");
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Welcome to " + bankName, 30, 40);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Your money is safe with us!", 30, 70);
    }

    public void stop() {
        System.out.println("stop() called");
    }

    public void destroy() {
        System.out.println("destroy() called");
    }
}
