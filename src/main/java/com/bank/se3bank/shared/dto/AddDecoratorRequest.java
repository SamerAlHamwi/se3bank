package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDecoratorRequest {
    
    @NotBlank(message = "نوع الديكور مطلوب")
    private String decoratorType; // OVERDRAFT_PROTECTION, INSURANCE, PREMIUM_SERVICES
    
    @NotNull(message = "معرف الحساب مطلوب")
    private Long accountId;
    
    // معاملات خاصة بـ OverdraftProtection
    private Double overdraftLimit;
    
    // معاملات خاصة بـ Insurance
    private Double coverageAmount;
    private String insuranceType; // FRAUD, THEFT, LOSS
    
    // معاملات خاصة بـ PremiumServices
    private String tierLevel; // GOLD, PLATINUM, DIAMOND
    
    private String description;
}