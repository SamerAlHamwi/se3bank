package com.bank.se3bank.accounts.model;

import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.users.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interest_strategy_name")
    private String interestStrategyName;

    @Column(name = "last_interest_calculation")
    private LocalDateTime lastInterestCalculation;

    @Column(name = "total_interest_earned")
    @Builder.Default
    private Double totalInterestEarned = 0.0;

    // إضافة هذه الدوال
    public void addInterest(Double interest) {
        if (interest > 0) {
            this.balance += interest;
            this.totalInterestEarned += interest;
            this.lastInterestCalculation = LocalDateTime.now();
        }
    }

    public Double getMonthlyInterestEarned() {
        // حساب الفائدة المكتسبة في آخر 30 يوم
        return 0.0; // سيتم حسابه من Service
    }

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // avoid serializing lazy proxy; expose via dedicated DTO if needed
    private User user;

    @Column(name = "balance", nullable = false)
    @Builder.Default
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "overdraft_limit")
    private Double overdraftLimit;

    @Column(name = "minimum_balance")
    private Double minimumBalance;

    // Composite Pattern - العلاقة مع AccountGroup
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    @JsonIgnore
    private AccountGroup parentGroup;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Transaction> incomingTransactions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", insertable = false, updatable = false)
    private AccountType accountType;

    // ========== Composite Pattern Abstract Methods ==========
    public abstract void add(Account account);
    public abstract void remove(Account account);
    public abstract boolean isComposite();
    public abstract Double getTotalBalance();
    public abstract AccountType getAccountType();

    // ========== Business Methods ==========
    public boolean canWithdraw(Double amount) {
        if (status != AccountStatus.ACTIVE) {
            return false;
        }

        Double availableBalance = balance;
        if (overdraftLimit != null && overdraftLimit > 0) {
            availableBalance += overdraftLimit;
        }

        return amount <= availableBalance;
    }

    public void deposit(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("المبلغ يجب أن يكون أكبر من صفر");
        }
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(Double amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalStateException("رصيد غير كافي أو الحساب غير نشط");
        }
        this.balance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void transferTo(Account target, Double amount) {
        if (this.equals(target)) {
            throw new IllegalArgumentException("لا يمكن التحويل لنفس الحساب");
        }
        this.withdraw(amount);
        target.deposit(amount);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== Composite Helper Methods ==========
    public boolean isInGroup() {
        return parentGroup != null;
    }

    public String getGroupName() {
        return parentGroup != null ? parentGroup.getGroupName() : "لا يوجد";
    }

    public abstract List<Account> getChildAccounts();
    public abstract int getChildCount();


    public Double getAvailableBalance() {
        Double available = balance;
        if (overdraftLimit != null && overdraftLimit > 0) {
            available += overdraftLimit;
        }
        return available;
    }

    // Builder based on AccountType
    public static AccountBuilder<?, ?> builder(AccountType type) {
        return switch (type) {
            case SAVINGS -> SavingsAccount.builder();
            case CHECKING -> CheckingAccount.builder();
            case LOAN -> LoanAccount.builder();
            case INVESTMENT -> InvestmentAccount.builder();
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
    }
}
