package bank.model;

import bank.exceptions.InsufficientFundsException;

/**
 * Interface for anything that can receive and give out money.
 * Variables in an interface are public static final by default.
 */
public interface Transactable {
    double MAX_LIMIT = 100000;   // max amount allowed in one transaction

    void deposit(double amount);

    void withdraw(double amount) throws InsufficientFundsException;
}
