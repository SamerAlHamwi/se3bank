package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * استراتيجية الفائدة البسيطة
 */
@Component
public class SimpleInterestStrategy implements InterestStrategy {
    
    private static final Double DEFAULT_RATE = 2.5; // 2.5% سنوياً
    
    @Override
    public Double calculateInterest(Double principal, Integer months, Account account) {
        if (principal <= 0 || months <= 0) {
            return 0.0;
        }
        
        // الفائدة البسيطة: I = P × r × t
        Double annualRate = getAnnualInterestRate();
        Double years = months / 12.0;
        
        return principal * annualRate * years;
    }
    
    @Override
    public String getStrategyName() {
        return "الفائدة البسيطة";
    }
    
    @Override
    public String getDescription() {
        return "حساب الفائدة البسيطة على المبلغ الأساسي فقط";
    }
    
    @Override
    public Double getAnnualInterestRate() {
        return DEFAULT_RATE / 100; // تحويل النسبة إلى قيمة عشرية
    }
    
    @Override
    public AccountType[] getSupportedAccountTypes() {
        return new AccountType[] {
            AccountType.SAVINGS,
            AccountType.CHECKING
        };
    }
    
    @Override
    public Double getMinimumBalance() {
        return 100.0; // الحد الأدنى 100 للفائدة
    }
    
    public Double calculateWithCustomRate(Double principal, Integer months, Double customRate) {
        if (principal <= 0 || months <= 0 || customRate <= 0) {
            return 0.0;
        }
        
        Double years = months / 12.0;
        return principal * (customRate / 100) * years;
    }
}