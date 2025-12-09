package com.bank.se3bank.accounts.model;

import com.bank.se3bank.shared.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("INVESTMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvestmentAccount extends Account {
    
    @Column(name = "risk_level")
    private String riskLevel; // LOW, MEDIUM, HIGH
    
    @Column(name = "investment_type")
    private String investmentType; // STOCKS, BONDS, MUTUAL_FUNDS
    
    @Column(name = "portfolio_value")
    private Double portfolioValue;
    
    @Column(name = "annual_return_rate")
    private Double annualReturnRate;
    
    @Override
    public void add(Account account) {
        throw new UnsupportedOperationException("لا يمكن إضافة حسابات إلى حساب استثمار فردي");
    }
    
    @Override
    public void remove(Account account) {
        throw new UnsupportedOperationException("لا يمكن إزالة حسابات من حساب استثمار فردي");
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public Double getTotalBalance() {
        return portfolioValue != null ? portfolioValue : getBalance();
    }
    
    @Override
    public AccountType getAccountType() {
        return AccountType.INVESTMENT;
    }
}