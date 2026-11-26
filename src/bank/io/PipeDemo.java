package bank.io;

import bank.model.Account;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 * Demo of pipe streams. One thread writes account details into a pipe
 * and another thread reads them from the other end.
 */
public class PipeDemo {

    public static void run(final Account[] accounts, final int count) throws IOException {
        final PipedOutputStream pipeOut = new PipedOutputStream();
        final PipedInputStream pipeIn = new PipedInputStream(pipeOut);

        Thread writer = new Thread() {
            public void run() {
                PrintStream ps = new PrintStream(pipeOut);
                for (int i = 0; i < count; i++) {
                    ps.println(accounts[i].getAccountNumber() + " - " + accounts[i].getHolderName());
                }
                ps.close();   // closing tells the reader that data is finished
            }
        };

        Thread reader = new Thread() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(pipeIn));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("  [Reader got] " + line);
                    }
                    br.close();
                } catch (IOException e) {
                    System.out.println("Pipe error: " + e.getMessage());
                }
            }
        };

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for pipe threads");
        }
    }
}
