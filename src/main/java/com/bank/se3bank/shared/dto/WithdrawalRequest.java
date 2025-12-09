package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalRequest {
    @NotBlank
    private String accountNumber;
    
    @NotNull
    @Min(1)
    private Double amount;
    
    private String description;
}