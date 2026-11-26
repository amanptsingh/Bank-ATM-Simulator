package bank.exceptions;

/** Thrown when the PIN is wrong or the account is locked. */
public class InvalidPinException extends Exception {
    public InvalidPinException(String message) {
        super(message);
    }
}
