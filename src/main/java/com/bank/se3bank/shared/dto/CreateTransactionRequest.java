package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTransactionRequest {
    @NotNull
    private TransactionType transactionType;
    
    private String fromAccountNumber;
    private String toAccountNumber;
    
    @NotNull
    @Min(1)
    private Double amount;
    
    private String description;
    private String referenceNumber;
    
    private Boolean isRecurring = false;
    private String recurrencePattern;
    private Integer recurrenceCount;
}