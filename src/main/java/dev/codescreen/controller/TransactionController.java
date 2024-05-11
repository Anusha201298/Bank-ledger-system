package dev.codescreen.controller;

import dev.codescreen.dto.balance;
import dev.codescreen.dto.TransactionRequest;
import dev.codescreen.dto.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    private static Map<String, Double> bankVault = new HashMap<>();


    // New endpoint to handle the ping request
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        try {
            // Simulate success by returning a success response with the current server time
            String message = "Pong! Server time is: " + java.time.LocalDateTime.now();
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            // If an exception occurs, return a server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PutMapping("/load")
    public ResponseEntity<TransactionResponse> depositRequest(@RequestBody TransactionRequest transactionRequest) {
        balance balance = transactionRequest.getTransactionAmount();
        if (balance.getDebitOrCredit().equals("CREDIT")) {
            // verify if user not exists
            if (!validateUserExistsInBankVault(transactionRequest.getUserId())) {
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), balance.getAmount());
            } else {
                Double currentBal = fetchUserBalFromBankVault(transactionRequest.getUserId());
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), currentBal + balance.getAmount());
            }
        } else {
            throw new UnsupportedOperationException("DEBIT!! Request is not Allowed, Please re-check");
        }
        TransactionResponse transactionResponse = createTransactionResponse(transactionRequest);
        transactionResponse.setResponseCode("APPROVED");
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @PutMapping("/authorization")
    public ResponseEntity<TransactionResponse> withdrawRequest(@RequestBody TransactionRequest transactionRequest) {
        balance balance = transactionRequest.getTransactionAmount();
        // verify if user not exists
        if (!validateUserExistsInBankVault(transactionRequest.getUserId())) {
            throw new UnsupportedOperationException("User not found!! Please load the user");
        }

        if (balance.getDebitOrCredit().equals("DEBIT")) {
            Double currentBal = fetchUserBalFromBankVault(transactionRequest.getUserId());
            if (currentBal >= balance.getAmount()) {
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), currentBal - balance.getAmount());
            } else {
                TransactionResponse transactionResponse = createTransactionResponse(transactionRequest);
                transactionResponse.setResponseCode("DECLINED");
                return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);            }
        } else {
            throw new UnsupportedOperationException("CREDIT!! Request is not Allowed, Please re-check");
        }
        TransactionResponse transactionResponse = createTransactionResponse(transactionRequest);
        transactionResponse.setResponseCode("APPROVED");
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @GetMapping("/reset")
    public String resetBankVault() {
        bankVault.clear();
        return "Successfully reset BankVault!!";
    }

    private TransactionResponse createTransactionResponse(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setUserId(transactionRequest.getUserId());
        transactionResponse.setMessageId(transactionRequest.getMessageId());
        transactionResponse.setAmount(transactionRequest.getTransactionAmount().getAmount());
        transactionResponse.setAvailableBalance(fetchUserBalFromBankVault(transactionRequest.getUserId()));
        return transactionResponse;
    }

    private void addOrUpdateUserToBankVault(String userId, Double amount) {
        bankVault.put(userId, amount);
    }

    private Double fetchUserBalFromBankVault(String userId) {
        return bankVault.get(userId);
    }

    private boolean validateUserExistsInBankVault(String userId) {
        return bankVault.containsKey(userId);
    }

    private void removeUserFromBankVault(String userId) {
        bankVault.remove(userId);
    }
}
