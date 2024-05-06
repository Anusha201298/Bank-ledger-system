package dev.codescreen.controller;

import dev.codescreen.dto.AmountDetails;
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

    @PutMapping("/load")
    public ResponseEntity<TransactionResponse> depositRequest(@RequestBody TransactionRequest transactionRequest) {
        AmountDetails amountDetails = transactionRequest.getTransactionAmount();
        if (amountDetails.getDebitOrCredit().equals("CREDIT")) {
            // verify if user not exists
            if (!validateUserExistsInBankVault(transactionRequest.getUserId())) {
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), amountDetails.getAmount());
            } else {
                Double currentBal = fetchUserBalFromBankVault(transactionRequest.getUserId());
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), currentBal + amountDetails.getAmount());
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
        AmountDetails amountDetails = transactionRequest.getTransactionAmount();
        // verify if user not exists
        if (!validateUserExistsInBankVault(transactionRequest.getUserId())) {
            throw new UnsupportedOperationException("User not found!! Please load the user");
        }

        if (amountDetails.getDebitOrCredit().equals("DEBIT")) {
            Double currentBal = fetchUserBalFromBankVault(transactionRequest.getUserId());
            if (currentBal >= amountDetails.getAmount()) {
                addOrUpdateUserToBankVault(transactionRequest.getUserId(), currentBal - amountDetails.getAmount());
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
