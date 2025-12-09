package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * استراتيجية الوديعة الثابتة (شهادات الإيداع)
 */
@Component
public class FixedDepositInterestStrategy implements InterestStrategy {
    
    @Override
    public Double calculateInterest(Double principal, Integer months, Account account) {
        if (principal <= 0 || months <= 0) {
            return 0.0;
        }
        
        // تحديد المعدل بناءً على المدة والمبلغ
        Double rate = getRateForTerm(months, principal);
        Double years = months / 12.0;
        
        // فائدة مركبة للودائع الثابتة
        Double amount = principal * Math.pow(1 + rate, years);
        return amount - principal;
    }
    
    @Override
    public String getStrategyName() {
        return "الوديعة الثابتة";
    }
    
    @Override
    public String getDescription() {
        return "فائدة الودائع الثابتة (شهادات الإيداع) بمعدلات مجزية";
    }
    
    @Override
    public Double getAnnualInterestRate() {
        return 4.0 / 100; // معدل افتراضي 4%
    }
    
    @Override
    public AccountType[] getSupportedAccountTypes() {
        return new AccountType[] {
            AccountType.INVESTMENT
        };
    }
    
    @Override
    public Double getMinimumBalance() {
        return 5000.0; // الحد الأدنى 5000 للوديعة الثابتة
    }
    
    @Override
    public Double getMaximumBalance() {
        return 1000000.0; // الحد الأقصى 1,000,000
    }
    
    private Double getRateForTerm(Integer months, Double amount) {
        // معدلات مختلفة حسب المدة والمبلغ
        if (months >= 60) { // 5 سنوات أو أكثر
            return amount > 50000 ? 5.5 / 100 : 5.0 / 100;
        } else if (months >= 36) { // 3-5 سنوات
            return amount > 50000 ? 4.5 / 100 : 4.0 / 100;
        } else if (months >= 24) { // 2-3 سنوات
            return 3.5 / 100;
        } else if (months >= 12) { // 1-2 سنوات
            return 3.0 / 100;
        } else { // أقل من سنة
            return 2.5 / 100;
        }
    }
    
    public Double calculateMaturityAmount(Double principal, Integer months) {
        Double rate = getRateForTerm(months, principal);
        Double years = months / 12.0;
        return principal * Math.pow(1 + rate, years);
    }
    
    public Double calculatePenaltyForEarlyWithdrawal(Double principal, Integer remainingMonths) {
        // غرامة السحب المبكر: 1% من المبلغ أو 50، أيهما أقل
        Double penalty = principal * 0.01;
        return Math.min(penalty, 50.0);
    }
}