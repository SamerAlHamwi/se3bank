package com.bank.se3bank.shared.dto;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {
    private String accountNumber;
    private AccountType accountType;
    private Double balance;
    private Double availableBalance;
    private AccountStatus status;
    private LocalDateTime createdAt;
    
    private String userName;
    private String userEmail;
    
    private List<String> decorators;
    private List<Transaction> recentTransactions;
    
    private Double totalDeposits;
    private Double totalWithdrawals;
    private Double netFlow;
}