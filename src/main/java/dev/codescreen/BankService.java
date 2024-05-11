package dev.codescreen;


public class BankService {
    private final transactionStore transactionStore = new transactionStore();

    public accountCreation createAccount(String accountId) {
        return new accountCreation(accountId, transactionStore);
    }

    // Other methods to interact with accountCreation objects
}