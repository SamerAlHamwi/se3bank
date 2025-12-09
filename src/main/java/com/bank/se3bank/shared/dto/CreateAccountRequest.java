package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {
    
    @NotNull(message = "نوع الحساب مطلوب")
    private AccountType accountType;
    
    @NotNull(message = "معرف المستخدم مطلوب")
    private Long userId;
    
    @Min(value = 0, message = "الرصيد الأولي يجب أن يكون صفر أو أكثر")
    private Double initialBalance = 0.0;
    
    private Double interestRate;
    private Double overdraftLimit;
    private Double minimumBalance;
    
    // Specific fields
    private Integer monthlyWithdrawalLimit; // للادخار
    private String riskLevel; // للاستثمار
    private String investmentType; // للاستثمار
    private Double loanAmount; // للقرض
    private Integer loanTermMonths; // للقرض
    private Double annualInterestRate; // للقرض
}