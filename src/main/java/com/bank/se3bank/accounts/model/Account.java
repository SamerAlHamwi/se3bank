package com.bank.se3bank.accounts.model;

import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.users.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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
    
    @Column(name = "last_interest_calculation")
    private LocalDateTime lastInterestCalculation;
    
    // Composite Pattern - العلاقة مع AccountGroup
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    private AccountGroup parentGroup;
    
    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> outgoingTransactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> incomingTransactions = new ArrayList<>();
    
    // Abstract methods for Composite Pattern
    public abstract void add(Account account);
    public abstract void remove(Account account);
    public abstract boolean isComposite();
    public abstract Double getTotalBalance();
    
    // Business methods
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
    
    public abstract AccountType getAccountType();


    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", insertable = false, updatable = false)
    private AccountType accountType;

}