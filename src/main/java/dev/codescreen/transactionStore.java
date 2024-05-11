package dev.codescreen;

import java.util.ArrayList;
import java.util.List;

public class transactionStore {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addEvent(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getEvents() {
        return transactions;
    }
}
