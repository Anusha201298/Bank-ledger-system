package dev.codescreen;


public class BankService {
    private final EventStore eventStore = new EventStore();

    public BankAccount createAccount(String accountId) {
        return new BankAccount(accountId, eventStore);
    }

    // Other methods to interact with BankAccount objects
}