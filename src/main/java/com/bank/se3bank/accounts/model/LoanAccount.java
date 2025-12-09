package com.bank.se3bank.accounts.model;

import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("LOAN")
@SuperBuilder
public class LoanAccount extends Account {
    
    @Column(name = "loan_amount", nullable = false)
    private Double loanAmount;
    
    @Column(name = "remaining_amount", nullable = false)
    private Double remainingAmount;
    
    @Column(name = "interest_rate", nullable = false)
    private Double annualInterestRate;
    
    @Column(name = "loan_term_months", nullable = false)
    private Integer loanTermMonths;
    
    @Column(name = "monthly_payment", nullable = false)
    private Double monthlyPayment;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;
    
    @Column(name = "missed_payments")
    @Builder.Default
    private Integer missedPayments = 0;
    
    @Column(name = "collateral_description")
    private String collateralDescription;
    
    @Override
    public void add(Account account) {
        throw new UnsupportedOperationException("لا يمكن إضافة حسابات إلى حساب قرض");
    }
    
    @Override
    public void remove(Account account) {
        throw new UnsupportedOperationException("لا يمكن إزالة حسابات من حساب قرض");
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public Double getTotalBalance() {
        return remainingAmount;
    }
    
    @Override
    public AccountType getAccountType() {
        return AccountType.LOAN;
    }
    
    public void makePayment(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("مبلغ الدفعة يجب أن يكون أكبر من صفر");
        }
        
        this.remainingAmount -= amount;
        
        if (remainingAmount <= 0) {
            setStatus(AccountStatus.CLOSED);
        }
    }
}