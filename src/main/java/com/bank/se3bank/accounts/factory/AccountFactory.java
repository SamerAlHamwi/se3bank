package com.bank.se3bank.accounts.factory;

import com.bank.se3bank.accounts.model.*;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * تطبيق Factory Pattern لإنشاء أنواع مختلفة من الحسابات
 * يخفف تعقيد إنشاء الكائنات ويوفر واجهة موحدة
 */
@Component
@Slf4j
public class AccountFactory {

    /**
     * إنشاء حساب بناءً على نوع الحساب المطلوب
     */
    public Account createAccount(CreateAccountRequest request, User user) {
        log.info("🏭 إنشاء حساب جديد من النوع: {} للمستخدم: {}", 
                request.getAccountType(), user.getUsername());

        Account account = switch (request.getAccountType()) {
            case SAVINGS -> createSavingsAccount(request, user);
            case CHECKING -> createCheckingAccount(request, user);
            case LOAN -> createLoanAccount(request, user);
            case INVESTMENT -> createInvestmentAccount(request, user);
            case BUSINESS -> createBusinessAccount(request, user);
        };

        // تعيين الخصائص العامة
        account.setAccountNumber(generateAccountNumber(request.getAccountType()));
        account.setUser(user);
        account.setBalance(request.getInitialBalance());
        account.setInterestRate(request.getInterestRate());
        account.setOverdraftLimit(request.getOverdraftLimit());
        account.setMinimumBalance(request.getMinimumBalance());

        log.info("✅ تم إنشاء حساب {} برقم: {}", 
                request.getAccountType().getArabicName(), 
                account.getAccountNumber());
        
        return account;
    }

    private SavingsAccount createSavingsAccount(CreateAccountRequest request, User user) {
        SavingsAccount account = new SavingsAccount();
        
        // إعدادات خاصة بحساب التوفير
        if (request.getMonthlyWithdrawalLimit() != null) {
            account.setMonthlyWithdrawalLimit(request.getMonthlyWithdrawalLimit());
        } else {
            account.setMonthlyWithdrawalLimit(5); // افتراضي: 5 عمليات سحب شهرياً
        }
        
        account.setMonthlyInterestCap(5000.0); // سقف فائدة شهري
        account.setWithdrawalsThisMonth(0);
        
        log.debug("تم إنشاء حساب توفير مع {} عملية سحب شهرياً", 
                account.getMonthlyWithdrawalLimit());
        
        return account;
    }

    private CheckingAccount createCheckingAccount(CreateAccountRequest request, User user) {
        CheckingAccount account = new CheckingAccount();
        
        // إعدادات خاصة بالحساب الجاري
        account.setCheckbookAvailable(true);
        account.setDebitCardNumber(generateDebitCardNumber());
        
        log.debug("تم إنشاء حساب جاري مع بطاقة خصم رقم: {}", 
                account.getDebitCardNumber());
        
        return account;
    }

    private LoanAccount createLoanAccount(CreateAccountRequest request, User user) {
        if (request.getLoanAmount() == null || request.getLoanAmount() <= 0) {
            throw new IllegalArgumentException("مبلغ القرض مطلوب ويجب أن يكون أكبر من صفر");
        }
        
        if (request.getLoanTermMonths() == null || request.getLoanTermMonths() <= 0) {
            throw new IllegalArgumentException("مدة القرض مطلوبة ويجب أن تكون أكبر من صفر");
        }
        
        LoanAccount account = new LoanAccount();
        
        // إعدادات خاصة بحساب القرض
        account.setLoanAmount(request.getLoanAmount());
        account.setRemainingAmount(request.getLoanAmount());
        account.setLoanTermMonths(request.getLoanTermMonths());
        
        if (request.getAnnualInterestRate() != null) {
            account.setAnnualInterestRate(request.getAnnualInterestRate());
        } else {
            account.setAnnualInterestRate(7.5); // نسبة فائدة افتراضية 7.5%
        }
        
        // حساب الدفعة الشهرية
        double monthlyRate = account.getAnnualInterestRate() / 100 / 12;
        double monthlyPayment = request.getLoanAmount() * monthlyRate * 
                Math.pow(1 + monthlyRate, request.getLoanTermMonths()) /
                (Math.pow(1 + monthlyRate, request.getLoanTermMonths()) - 1);
        
        account.setMonthlyPayment(Math.round(monthlyPayment * 100.0) / 100.0);
        
        // تواريخ القرض
        LocalDate startDate = LocalDate.now();
        account.setStartDate(startDate);
        account.setEndDate(startDate.plusMonths(request.getLoanTermMonths()));
        account.setNextPaymentDate(startDate.plusMonths(1));
        
        log.debug("تم إنشاء حساب قرض بمبلغ: {} لمدة: {} شهر، دفعة شهرية: {}", 
                request.getLoanAmount(), request.getLoanTermMonths(), account.getMonthlyPayment());
        
        return account;
    }

    private InvestmentAccount createInvestmentAccount(CreateAccountRequest request, User user) {
        InvestmentAccount account = new InvestmentAccount();
        
        // إعدادات خاصة بالحساب الاستثماري
        if (request.getRiskLevel() != null) {
            account.setRiskLevel(request.getRiskLevel());
        } else {
            account.setRiskLevel("MEDIUM"); // متوسط المخاطرة افتراضياً
        }
        
        if (request.getInvestmentType() != null) {
            account.setInvestmentType(request.getInvestmentType());
        } else {
            account.setInvestmentType("MUTUAL_FUNDS"); // صناديق استثمار افتراضياً
        }
        
        account.setPortfolioValue(request.getInitialBalance());
        account.setAnnualReturnRate(8.5); // عائد سنوي افتراضي 8.5%
        
        log.debug("تم إنشاء حساب استثماري بنوع: {} ومخاطرة: {}", 
                account.getInvestmentType(), account.getRiskLevel());
        
        return account;
    }

    private Account createBusinessAccount(CreateAccountRequest request, User user) {
        // في هذا المثال، BusinessAccount هو CheckingAccount مع مميزات إضافية
        CheckingAccount account = new CheckingAccount();
        account.setCheckbookAvailable(true);
        account.setDebitCardNumber(generateDebitCardNumber());
        
        log.debug("تم إنشاء حساب تجاري");
        
        return account;
    }

    /**
     * توليد رقم حساب فريد
     */
    private String generateAccountNumber(AccountType accountType) {
        String prefix = switch (accountType) {
            case SAVINGS -> "SAV";
            case CHECKING -> "CHK";
            case LOAN -> "LON";
            case INVESTMENT -> "INV";
            case BUSINESS -> "BUS";
        };
        
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        return prefix + timestamp + random;
    }

    /**
     * توليد رقم بطاقة خصم
     */
    private String generateDebitCardNumber() {
        StringBuilder cardNumber = new StringBuilder("4"); // فيزا تبدأ بـ 4
        
        for (int i = 0; i < 15; i++) {
            if (i % 4 == 0 && i > 0) {
                cardNumber.append(" ");
            }
            cardNumber.append((int) (Math.random() * 10));
        }
        
        return cardNumber.toString();
    }

    /**
     * إنشاء حساب باستخدام Builder Pattern (طريقة بديلة)
     */
    public Account createAccountWithBuilder(AccountType type, User user, Double initialBalance) {
        return Account.builder(type)
                .accountNumber(generateAccountNumber(type))
                .user(user)
                .balance(initialBalance != null ? initialBalance : 0.0)
                .build();
    }
}