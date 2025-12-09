package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.stereotype.Component;

/**
 * استراتيجية الفائدة المتدرجة (حسب شرائح الرصيد)
 */
@Component
public class TieredInterestStrategy implements InterestStrategy {
    
    // شرائح الرصيد ومعدلاتها
    private static final Tier[] TIERS = {
        new Tier(0.0, 10000.0, 1.0),    // 1% حتى 10,000
        new Tier(10000.0, 50000.0, 2.0), // 2% من 10,000 إلى 50,000
        new Tier(50000.0, 100000.0, 2.5), // 2.5% من 50,000 إلى 100,000
        new Tier(100000.0, Double.MAX_VALUE, 3.0) // 3% فوق 100,000
    };
    
    @Override
    public Double calculateInterest(Double principal, Integer months, Account account) {
        if (principal <= 0 || months <= 0) {
            return 0.0;
        }
        
        Double totalInterest = 0.0;
        Double remainingPrincipal = principal;
        
        // حساب الفائدة لكل شريحة
        for (Tier tier : TIERS) {
            if (remainingPrincipal <= 0) break;
            
            Double amountInTier = Math.min(remainingPrincipal, tier.max - tier.min);
            if (amountInTier > 0) {
                Double years = months / 12.0;
                Double interestForTier = amountInTier * (tier.rate / 100) * years;
                totalInterest += interestForTier;
                remainingPrincipal -= amountInTier;
            }
        }
        
        return totalInterest;
    }
    
    @Override
    public String getStrategyName() {
        return "الفائدة المتدرجة";
    }
    
    @Override
    public String getDescription() {
        return "فائدة متدرجة حسب شرائح الرصيد (كل شريحة معدل مختلف)";
    }
    
    @Override
    public Double getAnnualInterestRate() {
        // متوسط المعدل
        return 2.0 / 100;
    }
    
    @Override
    public AccountType[] getSupportedAccountTypes() {
        return new AccountType[] {
            AccountType.SAVINGS,
            AccountType.CHECKING,
            AccountType.INVESTMENT
        };
    }
    
    public Double getEffectiveRate(Double balance) {
        // حساب المعدل الفعلي للرصيد المحدد
        if (balance <= 0) return 0.0;
        
        Double totalWeightedRate = 0.0;
        Double remainingBalance = balance;
        
        for (Tier tier : TIERS) {
            if (remainingBalance <= 0) break;
            
            Double amountInTier = Math.min(remainingBalance, tier.max - tier.min);
            if (amountInTier > 0) {
                totalWeightedRate += (amountInTier / balance) * tier.rate;
                remainingBalance -= amountInTier;
            }
        }
        
        return totalWeightedRate / 100; // تحويل إلى نسبة عشرية
    }
    
    public Tier[] getTiers() {
        return TIERS.clone();
    }
    
    /**
     * كائن يمثل شريحة فائدة
     */
    public static class Tier {
        public final Double min;
        public final Double max;
        public final Double rate; // نسبة مئوية
        
        public Tier(Double min, Double max, Double rate) {
            this.min = min;
            this.max = max;
            this.rate = rate;
        }
        
        @Override
        public String toString() {
            return String.format("شريحة %.0f-%.0f: %.1f%%", min, max, rate);
        }
    }
}