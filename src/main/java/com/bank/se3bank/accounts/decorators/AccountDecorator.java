package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * تطبيق Decorator Pattern
 * فئة أساسية لإضافة ميزات ديناميكية للحسابات بدون تعديل الفئات الأصلية
 */
@Entity
@Table(name = "account_decorators")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "decorator_type")
@Getter
@Setter
public abstract class AccountDecorator extends Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "decorator_name", nullable = false)
    protected String decoratorName;
    
    @Column(name = "description")
    protected String description;
    
    @Column(name = "monthly_fee")
    protected Double monthlyFee;
    
    @Column(name = "is_active", nullable = false)
    protected Boolean isActive = true;
    
    @Column(name = "activated_at")
    protected LocalDateTime activatedAt;
    
    @Column(name = "deactivated_at")
    protected LocalDateTime deactivatedAt;
    
    // العلاقة مع الحساب المزين
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decorated_account_id", nullable = false)
    protected Account decoratedAccount;
    
    // ========== Constructor ==========
    
    protected AccountDecorator() {
        // Constructor for JPA
    }
    
    protected AccountDecorator(Account decoratedAccount, String decoratorName) {
        this.decoratedAccount = decoratedAccount;
        this.decoratorName = decoratorName;
        this.activatedAt = LocalDateTime.now();
        
        // نسخ الخصائص الأساسية من الحساب المزين
        if (decoratedAccount != null) {
            this.setAccountNumber(decoratedAccount.getAccountNumber());
            this.setUser(decoratedAccount.getUser());
            this.setBalance(decoratedAccount.getBalance());
            this.setStatus(decoratedAccount.getStatus());
            this.setCreatedAt(decoratedAccount.getCreatedAt());
            this.setInterestRate(decoratedAccount.getInterestRate());
            this.setOverdraftLimit(decoratedAccount.getOverdraftLimit());
            this.setMinimumBalance(decoratedAccount.getMinimumBalance());
        }
    }
    
    // ========== Decorator Pattern Methods ==========
    
    /**
     * تفعيل الديكورات
     */
    public void activate() {
        this.isActive = true;
        this.activatedAt = LocalDateTime.now();
        this.deactivatedAt = null;
    }
    
    /**
     * تعطيل الديكورات
     */
    public void deactivate() {
        this.isActive = false;
        this.deactivatedAt = LocalDateTime.now();
    }
    
    /**
     * تطبيق الرسوم الشهرية
     */
    public abstract void applyMonthlyFee();
    
    /**
     * التحقق إذا كان الحساب يدعم الميزة
     */
    public abstract boolean isFeatureSupported(String feature);
    
    /**
     * الحصول على قائمة الميزات المضافة
     */
    public abstract List<String> getAddedFeatures();
    
    // ========== Override Account Methods ==========
    
    @Override
    public AccountType getAccountType() {
        return decoratedAccount != null ? decoratedAccount.getAccountType() : null;
    }
    
    @Override
    public void deposit(Double amount) {
        if (decoratedAccount != null) {
            decoratedAccount.deposit(amount);
            this.setBalance(decoratedAccount.getBalance());
        }
    }
    
    @Override
    public void withdraw(Double amount) {
        if (decoratedAccount != null) {
            decoratedAccount.withdraw(amount);
            this.setBalance(decoratedAccount.getBalance());
        }
    }
    
    @Override
    public boolean canWithdraw(Double amount) {
        return decoratedAccount != null && decoratedAccount.canWithdraw(amount);
    }
    
    @Override
    public Double getTotalBalance() {
        return decoratedAccount != null ? decoratedAccount.getTotalBalance() : 0.0;
    }
    
    @Override
    public Double getAvailableBalance() {
        return decoratedAccount != null ? decoratedAccount.getAvailableBalance() : 0.0;
    }
    
    // ========== Getters with Decorator Logic ==========
    
    public String getOriginalAccountNumber() {
        return decoratedAccount != null ? decoratedAccount.getAccountNumber() : null;
    }
    
    public User getOriginalUser() {
        return decoratedAccount != null ? decoratedAccount.getUser() : null;
    }
    
    /**
     * الحصول على جميع الديكورات المطبقة على الحساب
     */
    public List<AccountDecorator> getAllDecorators() {
        List<AccountDecorator> decorators = new ArrayList<>();
        
        if (decoratedAccount instanceof AccountDecorator) {
            decorators.addAll(((AccountDecorator) decoratedAccount).getAllDecorators());
        }
        
        decorators.add(this);
        return decorators;
    }
    
    /**
     * التحقق إذا كان الحساب مزيناً بديكور معين
     */
    public boolean hasDecorator(String decoratorType) {
        if (this.getClass().getSimpleName().equals(decoratorType)) {
            return true;
        }
        
        if (decoratedAccount instanceof AccountDecorator) {
            return ((AccountDecorator) decoratedAccount).hasDecorator(decoratorType);
        }
        
        return false;
    }
    
    /**
     * إزالة جميع الديكورات وإرجاع الحساب الأصلي
     */
    public Account getOriginalAccount() {
        if (decoratedAccount instanceof AccountDecorator) {
            return ((AccountDecorator) decoratedAccount).getOriginalAccount();
        }
        return decoratedAccount;
    }
}