package com.bank.se3bank.interest.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.interest.strategy.InterestStrategy;
import com.bank.se3bank.shared.enums.AccountType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * تطبيق Strategy Pattern - Context
 * يدير استراتيجيات الفائدة ويوفر واجهة موحدة
 */
@Service
@Slf4j
public class InterestCalculator {
    
    private final Map<String, InterestStrategy> strategies = new ConcurrentHashMap<>();
    private final Map<AccountType, InterestStrategy> defaultStrategies = new ConcurrentHashMap<>();
    
    /**
     * تسجيل استراتيجية جديدة
     */
    public void registerStrategy(String strategyName, InterestStrategy strategy) {
        strategies.put(strategyName, strategy);
        log.info("📊 تم تسجيل استراتيجية فائدة: {}", strategy.getStrategyName());
    }
    
    /**
     * تعيين استراتيجية افتراضية لنوع حساب
     */
    public void setDefaultStrategy(AccountType accountType, InterestStrategy strategy) {
        defaultStrategies.put(accountType, strategy);
        log.info("⚙️ تم تعيين استراتيجية افتراضية لـ {}: {}", 
                accountType.getArabicName(), strategy.getStrategyName());
    }
    
    /**
     * حساب الفائدة لحساب باستخدام الاستراتيجية الحالية
     */
    public Double calculateInterest(Account account) {
        if (account == null || account.getBalance() <= 0) {
            return 0.0;
        }
        
        // الحصول على الاستراتيجية المناسبة
        InterestStrategy strategy = getStrategyForAccount(account);
        
        // حساب عمر الحساب بالأشهر
        Integer accountAgeInMonths = calculateAccountAgeInMonths(account);
        
        // حساب الفائدة
        Double interest = strategy.calculateInterest(
                account.getBalance(),
                accountAgeInMonths,
                account
        );
        
        log.info("💰 حساب فائدة الحساب {}: {} بـ {} = {:.2f}",
                account.getAccountNumber(),
                account.getBalance(),
                strategy.getStrategyName(),
                interest);
        
        return interest;
    }
    
    /**
     * حساب الفائدة مع استراتيجية محددة
     */
    public Double calculateInterestWithStrategy(Account account, String strategyName) {
        InterestStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("الاستراتيجية غير موجودة: " + strategyName);
        }
        
        Integer accountAgeInMonths = calculateAccountAgeInMonths(account);
        return strategy.calculateInterest(account.getBalance(), accountAgeInMonths, account);
    }
    
    /**
     * تغيير استراتيجية حساب معين
     */
    public void changeAccountStrategy(Account account, String newStrategyName) {
        InterestStrategy newStrategy = strategies.get(newStrategyName);
        if (newStrategy == null) {
            throw new IllegalArgumentException("الاستراتيجية غير موجودة: " + newStrategyName);
        }
        
        // التحقق من أن الاستراتيجية تدعم نوع الحساب
        if (!isStrategySupported(newStrategy, account.getAccountType())) {
            throw new IllegalStateException("الاستراتيجية لا تدعم نوع الحساب هذا");
        }
        
        log.info("🔄 تغيير استراتيجية الحساب {} من {} إلى {}",
                account.getAccountNumber(),
                account.getInterestStrategyName(),
                newStrategy.getStrategyName());
        
        account.setInterestStrategyName(newStrategyName);
    }
    
    /**
     * الحصول على الاستراتيجية المناسبة للحساب
     */
    private InterestStrategy getStrategyForAccount(Account account) {
        // إذا كان للحساب استراتيجية محددة
        if (account.getInterestStrategyName() != null) {
            InterestStrategy strategy = strategies.get(account.getInterestStrategyName());
            if (strategy != null && strategy.isActive()) {
                return strategy;
            }
        }
        
        // الاستراتيجية الافتراضية لنوع الحساب
        InterestStrategy defaultStrategy = defaultStrategies.get(account.getAccountType());
        if (defaultStrategy != null && defaultStrategy.isActive()) {
            return defaultStrategy;
        }
        
        // استراتيجية افتراضية عامة
        return strategies.get("simpleInterestStrategy");
    }
    
    /**
     * التحقق من دعم الاستراتيجية لنوع الحساب
     */
    public boolean isStrategySupported(InterestStrategy strategy, AccountType accountType) {
        for (AccountType supportedType : strategy.getSupportedAccountTypes()) {
            if (supportedType == accountType) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * حساب عمر الحساب بالأشهر
     */
    private Integer calculateAccountAgeInMonths(Account account) {
        LocalDateTime now = LocalDateTime.now();
        long months = ChronoUnit.MONTHS.between(account.getCreatedAt(), now);
        return (int) Math.max(1, months); // الحد الأدنى شهر واحد
    }
    
    /**
     * الحصول على جميع الاستراتيجيات المتاحة
     */
    public Map<String, InterestStrategy> getAllStrategies() {
        return new ConcurrentHashMap<>(strategies);
    }
    
    /**
     * الحصول على الاستراتيجيات المدعومة لنوع حساب
     */
    public Map<String, InterestStrategy> getSupportedStrategies(AccountType accountType) {
        Map<String, InterestStrategy> supported = new ConcurrentHashMap<>();
        
        strategies.forEach((name, strategy) -> {
            if (isStrategySupported(strategy, accountType) && strategy.isActive()) {
                supported.put(name, strategy);
            }
        });
        
        return supported;
    }
    
    /**
     * تعطيل/تفعيل استراتيجية
     */
    public void setStrategyActive(String strategyName, boolean active) {
        // Note: في تطبيق حقيقي، قد نحتاج إلى حقل active في الـ Strategy
        log.info("{} استراتيجية {}", active ? "تفعيل" : "تعطيل", strategyName);
    }
    
    /**
     * حساب الفائدة المستقبلية
     */
    public Double calculateFutureInterest(Account account, Integer futureMonths) {
        InterestStrategy strategy = getStrategyForAccount(account);
        return strategy.calculateInterest(account.getBalance(), futureMonths, account);
    }
    
    /**
     * مقارنة الفائدة بين استراتيجيتين
     */
    public InterestComparison compareStrategies(Account account, String strategy1Name, String strategy2Name) {
        InterestStrategy strategy1 = strategies.get(strategy1Name);
        InterestStrategy strategy2 = strategies.get(strategy2Name);
        
        if (strategy1 == null || strategy2 == null) {
            throw new IllegalArgumentException("إحدى الاستراتيجيات غير موجودة");
        }
        
        Integer months = calculateAccountAgeInMonths(account);
        Double interest1 = strategy1.calculateInterest(account.getBalance(), months, account);
        Double interest2 = strategy2.calculateInterest(account.getBalance(), months, account);
        
        return InterestComparison.builder()
                .strategy1Name(strategy1.getStrategyName())
                .strategy2Name(strategy2.getStrategyName())
                .interest1(interest1)
                .interest2(interest2)
                .difference(interest2 - interest1)
                .betterStrategy(interest1 > interest2 ? strategy1Name : strategy2Name)
                .build();
    }
    
    /**
     * DTO لمقارنة الاستراتيجيات
     */
    @lombok.Data
    @lombok.Builder
    public static class InterestComparison {
        private String strategy1Name;
        private String strategy2Name;
        private Double interest1;
        private Double interest2;
        private Double difference;
        private String betterStrategy;
    }
}