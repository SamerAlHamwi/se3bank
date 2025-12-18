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
@Table(name = "overdraft_decorators")
@Getter
@Setter
@NoArgsConstructor
public class OverdraftProtectionDecorator extends AccountDecorator {

    @Column(name = "overdraft_limit")
    private Double overdraftLimit;

    public OverdraftProtectionDecorator(Account decoratedAccount, Double overdraftLimit) {
        super(decoratedAccount);
        this.decoratorName = "Overdraft Protection";
        this.overdraftLimit = overdraftLimit;
        this.setMonthlyFee(5.0);
        this.setDescription("Allows withdrawing up to $" + overdraftLimit + " beyond available balance.");
        
        if (decoratedAccount.getOverdraftLimit() == null || decoratedAccount.getOverdraftLimit() < overdraftLimit) {
            decoratedAccount.setOverdraftLimit(overdraftLimit);
        }
    }

    @Override
    public List<String> getAddedFeatures() {
        return List.of("OVERDRAFT_PROTECTION", "NO_BOUNCED_CHECKS");
    }

    @Override
    public void applyMonthlyFee() {
        getDecoratedAccount().withdraw(getMonthlyFee());
    }
}
