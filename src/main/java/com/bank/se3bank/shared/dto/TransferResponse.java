package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private Boolean success;
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private Double newFromBalance;
    private Double newToBalance;
    private TransactionStatus status;
    private String message;
    private Long processingTimeMs;
    private LocalDateTime timestamp;
}