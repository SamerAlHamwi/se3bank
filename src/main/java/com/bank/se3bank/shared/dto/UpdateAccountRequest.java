package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.AccountStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateAccountRequest {
    private AccountStatus status;

    @Min(value = 0, message = "الرصيد يجب أن يكون صفراً أو أكبر")
    private Double balance;

    private Double interestRate;
    private Double overdraftLimit;
    private Double minimumBalance;
}

