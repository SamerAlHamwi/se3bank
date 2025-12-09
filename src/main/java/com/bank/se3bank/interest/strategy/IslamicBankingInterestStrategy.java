package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.stereotype.Component;

/**
 * استراتيجية المصرفية الإسلامية (بدون فائدة ربوية)
 */
@Component
public class IslamicBankingInterestStrategy implements InterestStrategy {
    
    private static final Double PROFIT_RATE = 1.5; // 1.5% أرباح مشاركة
    
    @Override
    public Double calculateInterest(Double principal, Integer months, Account account) {
        if (principal <= 0 || months <= 0) {
            return 0.0;
        }
        
        // في المصرفية الإسلامية: أرباح مشاركة وليست فائدة ربوية
        Double profitRate = PROFIT_RATE / 100;
        Double years = months / 12.0;
        
        // حساب الأرباح بناءً على أداء البنك
        Double baseProfit = principal * profitRate * years;
        
        // تعديل بناءً على عوامل إضافية
        Double adjustmentFactor = calculateAdjustmentFactor(account);
        
        return baseProfit * adjustmentFactor;
    }
    
    @Override
    public String getStrategyName() {
        return "المصرفية الإسلامية";
    }
    
    @Override
    public String getDescription() {
        return "نظام أرباح المشاركة حسب أحكام الشريعة الإسلامية";
    }
    
    @Override
    public Double getAnnualInterestRate() {
        return PROFIT_RATE / 100;
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
        return 1000.0;
    }
    
    private Double calculateAdjustmentFactor(Account account) {
        Double factor = 1.0;
        
        // عوامل التعديل:
        if (account.getBalance() > 50000) {
            factor *= 1.1; // +10% للحسابات الكبيرة
        }
        
        if (account.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusYears(1))) {
            factor *= 1.05; // +5% للحسابات القديمة
        }
        
        return factor;
    }
    
    public Double calculateHalalProfit(Double principal, Double bankProfitPercentage) {
        // أرباح مشاركة بناءً على ربحية البنك
        return principal * (bankProfitPercentage / 100);
    }
}