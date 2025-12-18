package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "account_decorators")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
public abstract class AccountDecorator {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decorator_seq")
    @SequenceGenerator(name = "decorator_seq", sequenceName = "account_decorators_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decorated_account_id", nullable = false)
    @JsonIgnore
    protected Account decoratedAccount;

    @Column(name = "decorator_name", nullable = false)
    protected String decoratorName;

    protected String description;

    @Column(name = "monthly_fee", nullable = false)
    protected Double monthlyFee = 0.0;

    @Column(name = "is_active", nullable = false)
    protected Boolean isActive = true;

    @Column(name = "activated_at")
    protected LocalDateTime activatedAt;

    @Column(name = "deactivated_at")
    protected LocalDateTime deactivatedAt;

    public AccountDecorator(Account decoratedAccount) {
        this.decoratedAccount = decoratedAccount;
        this.activatedAt = LocalDateTime.now();
    }

    public abstract List<String> getAddedFeatures();
    public abstract void applyMonthlyFee();

    public String getOriginalAccountNumber() {
        return decoratedAccount.getAccountNumber();
    }

    public String getAccountType() {
        return decoratedAccount.getAccountType() != null ? decoratedAccount.getAccountType().name() : "UNKNOWN";
    }

    public void activate() {
        this.isActive = true;
        this.deactivatedAt = null;
        this.activatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.deactivatedAt = LocalDateTime.now();
    }
    
    public boolean hasDecorator(String decoratorType) {
        return this.getClass().getSimpleName().equalsIgnoreCase(decoratorType) 
               || (decoratorName != null && decoratorName.toUpperCase().contains(decoratorType.toUpperCase()));
    }
}
