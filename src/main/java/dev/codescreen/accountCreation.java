package dev.codescreen;

import java.time.LocalDateTime;

public class accountCreation {
    private final String accountId;
    private double balance = 0;
    private final transactionStore transactionStore;

    public accountCreation(String accountId, transactionStore transactionStore) {
        this.accountId = accountId;
        this.transactionStore = transactionStore;
    }

    public double deposit(double amount) {
        Transaction transaction = new Transaction(accountId, amount, LocalDateTime.now(), "LOAD");
        transactionStore.addEvent(transaction);
        balance += amount;
        return balance;
    }

    public double withdraw(double amount) {
        if (balance >= amount) {
            Transaction transaction = new Transaction(accountId, -amount, LocalDateTime.now(), "AUTHORIZATION");
            transactionStore.addEvent(transaction);
            balance -= amount;
            return balance;
        } else {
            Transaction transaction = new Transaction(accountId, -amount, LocalDateTime.now(), "DECLINE");
            transactionStore.addEvent(transaction);
            return balance; // Balance remains unchanged on decline
        }
    }

    public double getBalance() {
        return balance;
    }
}
