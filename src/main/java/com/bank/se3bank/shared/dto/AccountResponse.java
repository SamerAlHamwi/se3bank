package com.bank.se3bank.shared.dto;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private Double balance;
    private Double availableBalance;
    private Double interestRate;
    private Double overdraftLimit;
    private Double minimumBalance;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .interestRate(account.getInterestRate())
                .overdraftLimit(account.getOverdraftLimit())
                .minimumBalance(account.getMinimumBalance())
                .userId(account.getUser() != null ? account.getUser().getId() : null)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

