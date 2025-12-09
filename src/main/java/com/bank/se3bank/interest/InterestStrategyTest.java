/* 
package com.bank.se3bank.interest;

import com.bank.se3bank.interest.strategy.*;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InterestStrategyTest {
    
    @Test
    void testSimpleInterestStrategy() {
        SimpleInterestStrategy strategy = new SimpleInterestStrategy();
        
        Account account = Account.builder()
                .accountNumber("TEST001")
                .balance(10000.0)
                .accountType(AccountType.SAVINGS)
                .createdAt(LocalDateTime.now())
                .build();
        
        Double interest = strategy.calculateInterest(10000.0, 12, account);
        
        // 10000 * 2.5% * 1 سنة = 250
        assertEquals(250.0, interest, 0.01);
        assertEquals("الفائدة البسيطة", strategy.getStrategyName());
        assertTrue(strategy.isActive());
    }
    
    @Test
    void testCompoundInterestStrategy() {
        CompoundInterestStrategy strategy = new CompoundInterestStrategy();
        
        Account account = Account.builder()
                .accountNumber("TEST002")
                .balance(10000.0)
                .accountType(AccountType.INVESTMENT)
                .createdAt(LocalDateTime.now())
                .build();
        
        Double interest = strategy.calculateInterest(10000.0, 12, account);
        
        // يجب أن تكون الفائدة المركبة أكبر من البسيطة لنفس الشروط
        assertTrue(interest > 250.0);
        assertEquals("الفائدة المركبة", strategy.getStrategyName());
    }
    
    @Test
    void testIslamicBankingStrategy() {
        IslamicBankingInterestStrategy strategy = new IslamicBankingInterestStrategy();
        
        Account account = Account.builder()
                .accountNumber("TEST003")
                .balance(50000.0)
                .accountType(AccountType.SAVINGS)
                .createdAt(LocalDateTime.now().minusYears(2))
                .build();
        
        Double profit = strategy.calculateInterest(50000.0, 24, account);
        
        // يجب أن يكون هناك ربح
        assertTrue(profit > 0);
        assertEquals("المصرفية الإسلامية", strategy.getStrategyName());
    }
    
    @Test
    void testTieredInterestStrategy() {
        TieredInterestStrategy strategy = new TieredInterestStrategy();
        
        Account account = Account.builder()
                .accountNumber("TEST004")
                .balance(75000.0)
                .accountType(AccountType.SAVINGS)
                .createdAt(LocalDateTime.now())
                .build();
        
        Double interest = strategy.calculateInterest(75000.0, 12, account);
        
        // يجب حساب الفائدة لكل شريحة
        assertTrue(interest > 0);
        
        // حساب المعدل الفعلي
        Double effectiveRate = strategy.getEffectiveRate(75000.0);
        assertTrue(effectiveRate > 0.01 && effectiveRate < 0.03);
    }
    
    @Test
    void testStrategySupport() {
        SimpleInterestStrategy simple = new SimpleInterestStrategy();
        CompoundInterestStrategy compound = new CompoundInterestStrategy();
        
        // التحقق من أنواع الحسابات المدعومة
        assertTrue(simple.getSupportedAccountTypes().length > 0);
        assertTrue(compound.getSupportedAccountTypes().length > 0);
        
        // التحقق من دعم نوع معين
        boolean supportsSavings = false;
        for (AccountType type : simple.getSupportedAccountTypes()) {
            if (type == AccountType.SAVINGS) {
                supportsSavings = true;
                break;
            }
        }
        assertTrue(supportsSavings);
    }
}
    */