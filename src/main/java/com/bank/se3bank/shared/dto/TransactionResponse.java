package com.bank.se3bank.shared.dto;

import com.bank.se3bank.transactions.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private String transactionType;
    private String status;
    private Double amount;
    private String fromAccount;
    private String toAccount;
    private String description;
    private String referenceNumber;
    private Long initiatedBy;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String failureReason;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType().name())
                .status(transaction.getStatus().name())
                .amount(transaction.getAmount())
                .fromAccount(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null)
                .toAccount(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null)
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .initiatedBy(transaction.getInitiatedBy())
                .approvedBy(transaction.getApprovedBy())
                .createdAt(transaction.getCreatedAt())
                .processedAt(transaction.getProcessedAt())
                .failureReason(transaction.getFailureReason())
                .build();
    }
}

