package com.bank.se3bank.shared.dto;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
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
public class UserSummary {
    private Long userId;
    private String userName;
    private String userEmail;
    
    private Integer totalAccounts;
    private Double totalBalance;
    
    private List<Account> accounts;
    private List<AccountGroup> groups;
    private List<Transaction> recentTransactions;
    
    private LocalDateTime lastLogin;
    private LocalDateTime memberSince;
}