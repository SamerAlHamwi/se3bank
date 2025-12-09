package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * ديكور حماية السحب على المكشوف
 * يسمح بالسحب حتى بعد نفاد الرصيد ضمن حد معين
 */
@Entity
@DiscriminatorValue("OVERDRAFT_PROTECTION")
@Getter
@Setter
@NoArgsConstructor
public class OverdraftProtectionDecorator extends AccountDecorator {
    
    @jakarta.persistence.Column(name = "overdraft_limit")
    private Double overdraftLimit;
    
    @jakarta.persistence.Column(name = "overdraft_fee_percentage")
    private Double overdraftFeePercentage = 5.0; // 5% رسوم على المكشوف
    
    @jakarta.persistence.Column(name = "max_overdraft_duration_days")
    private Integer maxOverdraftDurationDays = 30;
    
    @jakarta.persistence.Column(name = "overdraft_start_date")
    private LocalDateTime overdraftStartDate;
    
    @jakarta.persistence.Column(name = "current_overdraft_amount")
    private Double currentOverdraftAmount = 0.0;
    
    @jakarta.persistence.Column(name = "total_overdraft_fees")
    private Double totalOverdraftFees = 0.0;
    
    public OverdraftProtectionDecorator(Account decoratedAccount, Double overdraftLimit) {
        super(decoratedAccount, "حماية السحب على المكشوف");
        this.overdraftLimit = overdraftLimit;
        this.description = String.format("حماية السحب على المكشوف حتى %.2f", overdraftLimit);
        this.monthlyFee = 10.0; // رسوم شهرية ثابتة
    }
    
    // ========== Override Methods ==========
    
    @Override
    public void applyMonthlyFee() {
        if (getDecoratedAccount() != null && isActive) {
            // خصم الرسوم الشهرية
            getDecoratedAccount().withdraw(monthlyFee);
            setBalance(getDecoratedAccount().getBalance());
            
            // خصم رسوم المكشوف إذا كان هناك مكشوف
            if (currentOverdraftAmount > 0) {
                Double overdraftFee = currentOverdraftAmount * (overdraftFeePercentage / 100);
                getDecoratedAccount().withdraw(overdraftFee);
                setBalance(getDecoratedAccount().getBalance());
                totalOverdraftFees += overdraftFee;
            }
        }
    }

    @Override
    public void add(Account account) {

    }

    @Override
    public void remove(Account account) {

    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public boolean canWithdraw(Double amount) {
        Account account = getDecoratedAccount();
        if (account == null) return false;
        
        // حساب الرصيد المتاح مع المكشوف
        Double availableBalance = account.getBalance() + overdraftLimit - currentOverdraftAmount;
        return amount <= availableBalance && account.canWithdraw(amount);
    }
    
    @Override
    public void withdraw(Double amount) {
        Account account = getDecoratedAccount();
        if (account == null) return;
        
        // Double balanceBefore = account.getBalance();
        account.withdraw(amount);
        setBalance(account.getBalance());
        
        // إذا أصبح الرصيد سالباً، سجل المكشوف
        if (account.getBalance() < 0) {
            currentOverdraftAmount = Math.abs(account.getBalance());
            
            if (overdraftStartDate == null) {
                overdraftStartDate = LocalDateTime.now();
            }
        } else {
            currentOverdraftAmount = 0.0;
            overdraftStartDate = null;
        }
    }

    @Override
    public List<Account> getChildAccounts() {
        return List.of();
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Double getAvailableBalance() {
        Account account = getDecoratedAccount();
        if (account == null) return 0.0;
        
        return account.getBalance() + overdraftLimit - currentOverdraftAmount;
    }
    
    @Override
    public boolean isFeatureSupported(String feature) {
        return getAddedFeatures().contains(feature);
    }
    
    @Override
    public List<String> getAddedFeatures() {
        return Arrays.asList(
            "OVERDRAFT_PROTECTION",
            "NEGATIVE_BALANCE_ALLOWED",
            "OVERDRAFT_FEE_CALCULATION"
        );
    }
    
    // ========== Specific Methods ==========
    
    /**
     * دفع المكشوف الحالي
     */
    public void payOffOverdraft(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("المبلغ يجب أن يكون أكبر من صفر");
        }
        
        if (currentOverdraftAmount <= 0) {
            throw new IllegalStateException("لا يوجد مكشوف حالي للدفع");
        }
        
        Account account = getDecoratedAccount();
        if (account == null) return;
        
        // إيداع المبلغ لسداد المكشوف
        account.deposit(amount);
        setBalance(account.getBalance());
        
        // تحديث مبلغ المكشوف الحالي
        currentOverdraftAmount = Math.max(0, currentOverdraftAmount - amount);
        
        // إذا تم سداد المكشوف بالكامل
        if (currentOverdraftAmount <= 0) {
            overdraftStartDate = null;
        }
    }
    
    /**
     * الحصول على مبلغ المكشوف المتاح حالياً
     */
    public Double getAvailableOverdraft() {
        return Math.max(0, overdraftLimit - currentOverdraftAmount);
    }
    
    /**
     * التحقق إذا تجاوز مدة المكشوف المسموح بها
     */
    public boolean isOverdraftExpired() {
        if (overdraftStartDate == null) return false;
        
        LocalDateTime expiryDate = overdraftStartDate.plusDays(maxOverdraftDurationDays);
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    /**
     * زيادة حد المكشوف
     */
    public void increaseOverdraftLimit(Double additionalLimit) {
        if (additionalLimit <= 0) {
            throw new IllegalArgumentException("الحد الإضافي يجب أن يكون أكبر من صفر");
        }
        
        this.overdraftLimit += additionalLimit;
        this.monthlyFee += additionalLimit * 0.01; // زيادة 1% في الرسوم لكل 100 زيادة
    }
}