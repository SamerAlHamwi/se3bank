package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {
    
    @NotNull(message = "نوع المعاملة مطلوب")
    private TransactionType transactionType;
    
    private String fromAccountNumber;
    private String toAccountNumber;
    
    @NotNull(message = "المبلغ مطلوب")
    @Min(value = 1, message = "المبلغ يجب أن يكون أكبر من صفر")
    private Double amount;
    
    private String description;
    private String referenceNumber;
    
    // للمعاملات المجدولة
    private Boolean isRecurring = false;
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY
    private Integer recurrenceCount;
}