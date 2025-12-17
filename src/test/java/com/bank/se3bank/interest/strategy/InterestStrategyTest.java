package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.SavingsAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestStrategyTest {

    private final SimpleInterestStrategy simpleInterestStrategy = new SimpleInterestStrategy();
    private final CompoundInterestStrategy compoundInterestStrategy = new CompoundInterestStrategy();
    // Use concrete class SavingsAccount instead of abstract Account
    private final Account account = new SavingsAccount(); 

    @Test
    @DisplayName("Simple Interest: Calculate interest correctly")
    void testSimpleInterestCalculation() {
        Double principal = 1000.0;
        Integer months = 12;

        Double interest = simpleInterestStrategy.calculateInterest(principal, months, account);

        assertEquals(25.0, interest, 0.01);
    }

    @Test
    @DisplayName("Simple Interest: Zero balance or months returns zero")
    void testSimpleInterestZeroInput() {
        assertEquals(0.0, simpleInterestStrategy.calculateInterest(0.0, 12, account));
        assertEquals(0.0, simpleInterestStrategy.calculateInterest(1000.0, 0, account));
    }

    @Test
    @DisplayName("Compound Interest: Calculate interest correctly")
    void testCompoundInterestCalculation() {
        Double principal = 1000.0;
        Integer months = 12;

        Double interest = compoundInterestStrategy.calculateInterest(principal, months, account);

        assertEquals(30.42, interest, 0.01);
    }

    @Test
    @DisplayName("Compound Interest: Zero balance or months returns zero")
    void testCompoundInterestZeroInput() {
        assertEquals(0.0, compoundInterestStrategy.calculateInterest(0.0, 12, account));
        assertEquals(0.0, compoundInterestStrategy.calculateInterest(1000.0, 0, account));
    }
    
    @Test
    @DisplayName("Simple Interest: Custom Rate Calculation")
    void testSimpleInterestCustomRate() {
        Double interest = simpleInterestStrategy.calculateWithCustomRate(2000.0, 6, 5.0);
        assertEquals(50.0, interest, 0.01);
    }
}