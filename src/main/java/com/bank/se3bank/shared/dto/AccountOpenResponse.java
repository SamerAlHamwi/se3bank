package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpenResponse {
    private Boolean success;
    private String accountNumber;
    private AccountType accountType;
    private Double balance;
    private String message;
    private Long processingTimeMs;
    private LocalDateTime timestamp;
}