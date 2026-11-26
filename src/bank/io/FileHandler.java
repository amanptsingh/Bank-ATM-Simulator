package bank.io;

import bank.model.Account;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

/**
 * All the file work of the project is done here.
 * Uses: byte streams, filter streams, object serialization/deserialization.
 */
public class FileHandler {
    public static final String DATA_FILE = "accounts.dat";
    public static final String BACKUP_FILE = "accounts_backup.dat";
    public static final String LOG_FILE = "transactions.log";
    public static final String SUMMARY_FILE = "summary.dat";

    // ---------- Serialization ----------
    public static void saveAccounts(Account[] accounts, int count) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
            out.writeInt(count);
            for (int i = 0; i < count; i++) {
                out.writeObject(accounts[i]);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // ---------- Deserialization ----------
    public static int loadAccounts(Account[] accounts) throws IOException, ClassNotFoundException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return 0;   // first run, nothing to load
        }
        int count = 0;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            count = in.readInt();
            for (int i = 0; i < count; i++) {
                accounts[i] = (Account) in.readObject();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return count;
    }

    // ---------- Output stream + PrintStream (filter stream) ----------
    public static void writeLog(String message) {
        PrintStream ps = null;
        try {
            // true = append at the end of the file
            ps = new PrintStream(new FileOutputStream(LOG_FILE, true));
            ps.println(new java.util.Date() + " : " + message);
        } catch (IOException e) {
            System.out.println("Could not write log: " + e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    // ---------- Input stream + BufferedReader ----------
    public static String readLog() {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            return "No log file yet.";
        }
        String all = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                all += line + "\n";
            }
        } catch (IOException e) {
            all = "Error reading log: " + e.getMessage();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing file");
            }
        }
        return all;
    }

    // ---------- DataOutputStream / DataInputStream (filter streams) ----------
    public static void writeSummary(Account[] accounts, int count) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(SUMMARY_FILE));
        try {
            for (int i = 0; i < count; i++) {
                dos.writeInt(accounts[i].getAccountNumber());
                dos.writeUTF(accounts[i].getHolderName());
                dos.writeDouble(accounts[i].getBalance());
            }
        } finally {
            dos.close();
        }
    }

    public static String readSummary() throws IOException {
        File file = new File(SUMMARY_FILE);
        if (!file.exists()) {
            return "No summary file yet. Choose 'Backup' first.";
        }
        String result = "";
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        try {
            while (true) {
                int no = dis.readInt();
                String name = dis.readUTF();
                double bal = dis.readDouble();
                result += no + "  " + name + "  Rs." + bal + "\n";
            }
        } catch (EOFException e) {
            // reached end of file, this is normal for this method
        } finally {
            dis.close();
        }
        return result;
    }

    // ---------- Copying a file byte by byte ----------
    public static void backupData() throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(DATA_FILE);
            out = new FileOutputStream(BACKUP_FILE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
