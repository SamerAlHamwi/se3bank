/* 
package com.bank.se3bank.accounts;

import com.bank.se3bank.accounts.model.*;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.users.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompositePatternTest {

    @Test
    void testCompositePatternStructure() {
        // إنشاء حسابات فردية (Leaf nodes)
        SavingsAccount savings = new SavingsAccount();
        savings.setAccountNumber("SAV001");
        savings.setBalance(5000.0);
        
        CheckingAccount checking = new CheckingAccount();
        checking.setAccountNumber("CHK001");
        checking.setBalance(3000.0);
        
        // التحقق أنها ليست مركبة
        assertFalse(savings.isComposite());
        assertFalse(checking.isComposite());
        
        // محاولة إضافة حساب إلى حساب فردي (يجب أن ترمي استثناء)
        assertThrows(UnsupportedOperationException.class, () -> {
            savings.add(checking);
        });
    }

    @Test
    void testAccountGroupComposite() {
        // إنشاء مجموعة حسابات (Composite)
        AccountGroup familyGroup = AccountGroup.builder()
                .groupName("العائلة")
                .groupType("FAMILY")
                .build();
        
        familyGroup.setAccountNumber("GRP001");
        
        // التحقق أنها مركبة
        assertTrue(familyGroup.isComposite());
        
        // إنشاء حسابات فرعية
        SavingsAccount child1 = new SavingsAccount();
        child1.setAccountNumber("CHILD1");
        child1.setBalance(1000.0);
        
        CheckingAccount child2 = new CheckingAccount();
        child2.setAccountNumber("CHILD2");
        child2.setBalance(2000.0);
        
        // إضافة الحسابات إلى المجموعة
        familyGroup.add(child1);
        familyGroup.add(child2);
        
        // التحقق من عدد الحسابات الفرعية
        assertEquals(2, familyGroup.getChildCount());
        assertEquals(2, familyGroup.getChildAccounts().size());
        
        // حساب إجمالي الرصيد
        assertEquals(3000.0, familyGroup.getTotalBalance(), 0.01);
        
        // إزالة حساب من المجموعة
        familyGroup.remove(child1);
        assertEquals(1, familyGroup.getChildCount());
        assertEquals(2000.0, familyGroup.getTotalBalance(), 0.01);
    }

    @Test
    void testGroupStatistics() {
        AccountGroup group = AccountGroup.builder()
                .groupName("اختبار")
                .groupType("TEST")
                .build();
        
        // إضافة حسابات بحالات مختلفة
        SavingsAccount activeAccount = new SavingsAccount();
        activeAccount.setAccountNumber("ACT001");
        activeAccount.setBalance(1000.0);
        activeAccount.setStatus(AccountStatus.ACTIVE);
        
        CheckingAccount frozenAccount = new CheckingAccount();
        frozenAccount.setAccountNumber("FRZ001");
        frozenAccount.setBalance(2000.0);
        frozenAccount.setStatus(AccountStatus.FROZEN);
        
        group.add(activeAccount);
        group.add(frozenAccount);
        
        // اختبار الإحصائيات
        assertEquals(2, group.getChildCount());
        assertTrue(group.containsAccount("ACT001"));
        assertFalse(group.areAllAccountsActive());
        
        // اختبار الحسابات النشطة فقط
        assertEquals(1000.0, 
                group.getChildAccounts().stream()
                        .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                        .mapToDouble(Account::getBalance)
                        .sum());
    }

    @Test
    void testTransferWithinGroup() {
        AccountGroup group = AccountGroup.builder()
                .groupName("تحويلات")
                .groupType("TRANSFER")
                .build();
        
        SavingsAccount fromAccount = new SavingsAccount();
        fromAccount.setAccountNumber("FROM001");
        fromAccount.setBalance(1000.0);
        
        CheckingAccount toAccount = new CheckingAccount();
        toAccount.setAccountNumber("TO001");
        toAccount.setBalance(500.0);
        
        group.add(fromAccount);
        group.add(toAccount);
        
        // التحويل داخل المجموعة
        group.transferWithinGroup("FROM001", "TO001", 300.0);
        
        assertEquals(700.0, fromAccount.getBalance(), 0.01);
        assertEquals(800.0, toAccount.getBalance(), 0.01);
        
        // التحقق من رصيد المجموعة الكلي لم يتغير
        assertEquals(1500.0, group.getTotalBalance(), 0.01);
    }
}
    */