package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "insurance_decorators")
@Getter
@Setter
@NoArgsConstructor
public class InsuranceDecorator extends AccountDecorator {

    @Column(name = "coverage_amount")
    private Double coverageAmount;
    
    @Column(name = "insurance_type")
    private String insuranceType; 

    public InsuranceDecorator(Account decoratedAccount, Double coverageAmount, String insuranceType) {
        super(decoratedAccount);
        this.decoratorName = "Insurance - " + insuranceType;
        this.coverageAmount = coverageAmount;
        this.insuranceType = insuranceType;
        this.setMonthlyFee(calculateFee());
        this.setDescription("Provides insurance coverage of $" + coverageAmount + " for " + insuranceType);
    }

    @Override
    public List<String> getAddedFeatures() {
        return List.of("INSURANCE_COVERAGE", "PEACE_OF_MIND");
    }

    @Override
    public void applyMonthlyFee() {
        double fee = calculateFee();
        getDecoratedAccount().withdraw(fee);
    }

    private double calculateFee() {
        return coverageAmount * 0.001;
    }
}
