package dev.codescreen.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String userId;
    private String messageId;
    private balance transactionAmount;
}
