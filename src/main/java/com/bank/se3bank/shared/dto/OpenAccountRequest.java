package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OpenAccountRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private AccountType accountType;
    
    private Double initialBalance = 0.0;
    private Double interestRate;
    private Double overdraftLimit;
    
    private List<AddDecoratorRequest> decorators;
}