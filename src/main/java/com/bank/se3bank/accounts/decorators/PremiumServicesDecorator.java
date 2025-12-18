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
@Table(name = "premium_decorators")
@Getter
@Setter
@NoArgsConstructor
public class PremiumServicesDecorator extends AccountDecorator {

    @Column(name = "tier_level")
    private String tierLevel;

    public PremiumServicesDecorator(Account decoratedAccount, String tierLevel) {
        super(decoratedAccount);
        this.decoratorName = "Premium Services - " + tierLevel;
        this.tierLevel = tierLevel;
        this.setMonthlyFee(calculateFee());
        this.setDescription("Access to " + tierLevel + " tier exclusive services.");
    }

    @Override
    public List<String> getAddedFeatures() {
        return switch (tierLevel.toUpperCase()) {
            case "DIAMOND" -> List.of("PRIORITY_SUPPORT", "LOWER_LOAN_RATES", "FREE_TRANSFERS", "CONCIERGE");
            case "PLATINUM" -> List.of("PRIORITY_SUPPORT", "LOWER_LOAN_RATES", "FREE_TRANSFERS");
            default -> List.of("PRIORITY_SUPPORT", "LOWER_LOAN_RATES");
        };
    }

    @Override
    public void applyMonthlyFee() {
        getDecoratedAccount().withdraw(getMonthlyFee());
    }

    private double calculateFee() {
        return switch (tierLevel.toUpperCase()) {
            case "DIAMOND" -> 50.0;
            case "PLATINUM" -> 25.0;
            default -> 10.0;
        };
    }
}
