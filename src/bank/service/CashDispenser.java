package bank.service;

/** Decides how many notes of each type the ATM gives out. Uses a simple array. */
public class CashDispenser {
    private static final int[] NOTES = {2000, 500, 200, 100};

    // ATM only gives amounts that are multiples of 100
    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount % 100 == 0;
    }

    public static int[] getNoteCount(int amount) {
        int[] counts = new int[NOTES.length];
        for (int i = 0; i < NOTES.length; i++) {
            counts[i] = amount / NOTES[i];
            amount = amount % NOTES[i];
        }
        return counts;
    }

    public static String describe(int amount) {
        int[] counts = getNoteCount(amount);
        String result = "Please collect your cash:\n";
        for (int i = 0; i < NOTES.length; i++) {
            if (counts[i] > 0) {
                result += "  Rs." + NOTES[i] + " x " + counts[i] + "\n";
            }
        }
        return result;
    }
}
