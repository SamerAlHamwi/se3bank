package com.bank.se3bank.interest.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.interest.strategy.*;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * خدمة إدارة الفائدة
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterestService {
    
    private final InterestCalculator interestCalculator;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final SimpleInterestStrategy simpleInterestStrategy;
    private final CompoundInterestStrategy compoundInterestStrategy;
    private final IslamicBankingInterestStrategy islamicBankingInterestStrategy;
    private final FixedDepositInterestStrategy fixedDepositInterestStrategy;
    private final TieredInterestStrategy tieredInterestStrategy;
    
    /**
     * تهيئة الاستراتيجيات عند بدء التشغيل
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("📈 تهيئة نظام الفائدة (Strategy Pattern)...");
        
        // تسجيل جميع الاستراتيجيات
        interestCalculator.registerStrategy("simpleInterestStrategy", simpleInterestStrategy);
        interestCalculator.registerStrategy("compoundInterestStrategy", compoundInterestStrategy);
        interestCalculator.registerStrategy("islamicBankingInterestStrategy", islamicBankingInterestStrategy);
        interestCalculator.registerStrategy("fixedDepositInterestStrategy", fixedDepositInterestStrategy);
        interestCalculator.registerStrategy("tieredInterestStrategy", tieredInterestStrategy);
        
        // تعيين الاستراتيجيات الافتراضية
        interestCalculator.setDefaultStrategy(AccountType.SAVINGS, simpleInterestStrategy);
        interestCalculator.setDefaultStrategy(AccountType.CHECKING, tieredInterestStrategy);
        interestCalculator.setDefaultStrategy(AccountType.INVESTMENT, compoundInterestStrategy);
        interestCalculator.setDefaultStrategy(AccountType.LOAN, simpleInterestStrategy);
        interestCalculator.setDefaultStrategy(AccountType.BUSINESS, tieredInterestStrategy);
        
        log.info("✅ تم تسجيل {} استراتيجية فائدة", interestCalculator.getAllStrategies().size());
    }
    
    /**
     * تطبيق الفائدة على حساب معين
     */
    @Transactional
    public Double applyInterestToAccount(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        
        // حساب الفائدة
        Double interestAmount = interestCalculator.calculateInterest(account);
        
        if (interestAmount > 0) {
            // إضافة الفائدة إلى الحساب
            Double oldBalance = account.getBalance();
            account.deposit(interestAmount);
            accountService.updateBalance(accountId, account.getBalance());
            
            // تسجيل معاملة الفائدة
            transactionService.createDepositTransaction(
                    account,
                    interestAmount,
                    "فائدة شهرية - " + getStrategyName(account)
            );
            
            // إرسال إشعار
            notificationService.sendInterestAddedNotification(
                    account.getUser(),
                    account,
                    interestAmount
            );
            
            // تحديث وقت آخر حساب فائدة
            account.setLastInterestCalculation(LocalDateTime.now());
            
            log.info("💰 تم تطبيق فائدة {} على الحساب {} (من {} إلى {})",
                    interestAmount,
                    account.getAccountNumber(),
                    oldBalance,
                    account.getBalance());
            
            return interestAmount;
        }
        
        return 0.0;
    }
    
    /**
     * تطبيق الفائدة على جميع الحسابات المؤهلة
     */
    @Transactional
    @Scheduled(cron = "0 0 1 1 * ?") // أول كل شهر في 1:00 صباحاً
    public void applyInterestToAllAccounts() {
        log.info("🏦 بدء تطبيق الفائدة الشهرية على جميع الحسابات...");
        
        List<Account> allAccounts = accountService.getAllAccounts();
        int successCount = 0;
        Double totalInterest = 0.0;
        
        for (Account account : allAccounts) {
            try {
                // التحقق من أهلية الحساب للفائدة
                if (isAccountEligibleForInterest(account)) {
                    Double interest = applyInterestToAccount(account.getId());
                    if (interest > 0) {
                        successCount++;
                        totalInterest += interest;
                    }
                }
            } catch (Exception e) {
                log.error("❌ فشل تطبيق الفائدة على الحساب {}: {}",
                        account.getAccountNumber(), e.getMessage());
            }
        }
        
        log.info("✅ تم تطبيق فائدة إجمالية {} على {} حساب",
                totalInterest, successCount);
    }
    
    /**
     * تغيير استراتيجية الفائدة لحساب
     */
    @Transactional
    public void changeAccountInterestStrategy(Long accountId, String newStrategyName) {
        Account account = accountService.getAccountById(accountId);
        
        interestCalculator.changeAccountStrategy(account, newStrategyName);
        accountService.updateAccount(account);
        
        log.info("🔄 تم تغيير استراتيجية الفائدة للحساب {} إلى {}",
                account.getAccountNumber(), newStrategyName);
    }
    
    /**
     * الحصول على استراتيجيات الفائدة المدعومة لنوع حساب
     */
    public Map<String, com.bank.se3bank.interest.strategy.InterestStrategy> 
            getSupportedStrategies(AccountType accountType) {
        return interestCalculator.getSupportedStrategies(accountType);
    }
    
    /**
     * حساب الفائدة المستقبلية لحساب
     */
    public Double calculateFutureInterest(Long accountId, Integer months) {
        Account account = accountService.getAccountById(accountId);
        return interestCalculator.calculateFutureInterest(account, months);
    }
    
    /**
     * مقارنة استراتيجيات الفائدة لحساب
     */
    public InterestCalculator.InterestComparison compareStrategiesForAccount(
            Long accountId, String strategy1Name, String strategy2Name) {
        Account account = accountService.getAccountById(accountId);
        return interestCalculator.compareStrategies(account, strategy1Name, strategy2Name);
    }
    
    /**
     * الحصول على تقرير الفائدة لحساب
     */
    public InterestReport getInterestReport(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        
        Double monthlyInterest = interestCalculator.calculateInterest(account);
        Double yearlyInterest = monthlyInterest * 12;
        Double projectedInterest5Years = calculateFutureInterest(accountId, 60);
        
        String currentStrategy = getStrategyName(account);
        Double effectiveRate = calculateEffectiveInterestRate(account);
        
        return InterestReport.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .currentBalance(account.getBalance())
                .currentStrategy(currentStrategy)
                .effectiveAnnualRate(effectiveRate * 100) // نسبة مئوية
                .monthlyInterest(monthlyInterest)
                .yearlyInterest(yearlyInterest)
                .projected5YearInterest(projectedInterest5Years)
                .lastInterestCalculation(account.getLastInterestCalculation())
                .nextInterestDate(LocalDateTime.now().plusMonths(1).withDayOfMonth(1))
                .build();
    }
    
    /**
     * حساب معدل الفائدة الفعلي للحساب
     */
    public Double calculateEffectiveInterestRate(Account account) {
        Double monthlyInterest = interestCalculator.calculateInterest(account);
        if (monthlyInterest <= 0 || account.getBalance() <= 0) {
            return 0.0;
        }
        
        // معدل شهري
        Double monthlyRate = monthlyInterest / account.getBalance();
        // تحويل إلى معدل سنوي
        return monthlyRate * 12;
    }
    
    // ========== Helper Methods ==========
    
    // 📁 src/main/java/com/bank/se3bank/interest/service/InterestService.java (الجزء المصحح)
    // ========== Helper Methods ==========
    
    private boolean isAccountEligibleForInterest(Account account) {
        // التحقق من أهلية الحساب للفائدة
        return account.getBalance() > 0 &&
               account.getStatus() == AccountStatus.ACTIVE && // ✅ التصحيح هنا
               (account.getLastInterestCalculation() == null ||
                account.getLastInterestCalculation().isBefore(LocalDateTime.now().minusDays(28)));
    }
    
    private String getStrategyName(Account account) {
        if (account.getInterestStrategyName() != null) {
            com.bank.se3bank.interest.strategy.InterestStrategy strategy = 
                    interestCalculator.getAllStrategies().get(account.getInterestStrategyName());
            if (strategy != null) {
                return strategy.getStrategyName();
            }
        }
        return "الافتراضية";
    }
    
    /**
     * DTO لتقرير الفائدة
     */
    @lombok.Data
    @lombok.Builder
    public static class InterestReport {
        private String accountNumber;
        private AccountType accountType;
        private Double currentBalance;
        private String currentStrategy;
        private Double effectiveAnnualRate; // نسبة مئوية
        private Double monthlyInterest;
        private Double yearlyInterest;
        private Double projected5YearInterest;
        private LocalDateTime lastInterestCalculation;
        private LocalDateTime nextInterestDate;
    }
}