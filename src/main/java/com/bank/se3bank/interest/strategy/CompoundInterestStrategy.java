package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.stereotype.Component;

/**
 * استراتيجية الفائدة المركبة
 */
@Component
public class CompoundInterestStrategy implements InterestStrategy {
    
    private static final Double DEFAULT_RATE = 3.0; // 3% سنوياً
    private static final Integer COMPOUNDING_PERIODS = 12; // مركب شهرياً
    
    @Override
    public Double calculateInterest(Double principal, Integer months, Account account) {
        if (principal <= 0 || months <= 0) {
            return 0.0;
        }
        
        // الفائدة المركبة: A = P (1 + r/n)^(nt)
        Double annualRate = getAnnualInterestRate();
        Double ratePerPeriod = annualRate / COMPOUNDING_PERIODS;
        Integer totalPeriods = months; // مركب شهرياً
        
        Double amount = principal * Math.pow(1 + ratePerPeriod, totalPeriods);
        return amount - principal; // الفائدة فقط
    }
    
    @Override
    public String getStrategyName() {
        return "الفائدة المركبة";
    }
    
    @Override
    public String getDescription() {
        return "حساب الفائدة المركبة شهرياً (الفائدة على الفائدة)";
    }
    
    @Override
    public Double getAnnualInterestRate() {
        return DEFAULT_RATE / 100;
    }
    
    @Override
    public AccountType[] getSupportedAccountTypes() {
        return new AccountType[] {
            AccountType.SAVINGS,
            AccountType.INVESTMENT
        };
    }
    
    @Override
    public Double getMinimumBalance() {
        return 500.0; // الحد الأدنى 500 للفائدة المركبة
    }
    
    public Double calculateFutureValue(Double principal, Integer months) {
        Double annualRate = getAnnualInterestRate();
        Double ratePerPeriod = annualRate / COMPOUNDING_PERIODS;
        Integer totalPeriods = months;
        
        return principal * Math.pow(1 + ratePerPeriod, totalPeriods);
    }
    
    public Integer calculateMonthsToDouble(Double principal) {
        // قاعدة 72: عدد السنوات لتضاعف المال = 72 / معدل الفائدة
        Double yearsToDouble = 72 / (DEFAULT_RATE);
        return (int) Math.ceil(yearsToDouble * 12);
    }
}