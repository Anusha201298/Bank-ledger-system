package dev.codescreen.dto;

import lombok.Data;
@Data
public class TransactionResponse {
    private String userId;
    private String messageId;
    private String responseCode;
    private Double amount;
    private Double availableBalance;
}
