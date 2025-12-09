package com.bank.se3bank.shared.dto;

import com.bank.se3bank.accounts.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupAccountResponse {
    private Long groupId;
    private String groupName;
    private String groupType;
    private Integer totalAccounts;
    private Double totalBalance;
    private List<Account> accounts;
    private boolean isComposite;
}