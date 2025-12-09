package com.bank.se3bank.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceResponse {
    private Long accountId;
    private String accountNumber;
    private Double balance;
    private Double availableBalance;
    private String currency;
}

