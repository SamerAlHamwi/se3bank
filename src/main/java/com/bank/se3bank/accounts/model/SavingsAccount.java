package com.bank.se3bank.accounts.model;

import com.bank.se3bank.shared.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SAVINGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SavingsAccount extends Account {
    
    @Column(name = "monthly_withdrawal_limit")
    @Builder.Default
    private Integer monthlyWithdrawalLimit = 5;
    
    @Column(name = "withdrawals_this_month")
    @Builder.Default
    private Integer withdrawalsThisMonth = 0;
    
    @Column(name = "monthly_interest_cap")
    private Double monthlyInterestCap;
    
    @Override
    public void add(Account account) {
        throw new UnsupportedOperationException("لا يمكن إضافة حسابات إلى حساب توفير فردي");
    }
    
    @Override
    public void remove(Account account) {
        throw new UnsupportedOperationException("لا يمكن إزالة حسابات من حساب توفير فردي");
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public Double getTotalBalance() {
        return getBalance();
    }
    
    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }
    
    public void resetMonthlyWithdrawals() {
        this.withdrawalsThisMonth = 0;
    }
    
    public boolean canWithdrawThisMonth() {
        return withdrawalsThisMonth < monthlyWithdrawalLimit;
    }
}