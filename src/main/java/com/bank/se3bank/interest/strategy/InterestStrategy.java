package com.bank.se3bank.interest.strategy;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountType;

/**
 * تطبيق Strategy Pattern
 * واجهة استراتيجية حساب الفائدة
 */
public interface InterestStrategy {
    
    /**
     * حساب الفائدة لمبلغ معين
     * @param principal المبلغ الأساسي
     * @param months عدد الأشهر
     * @param account الحساب (للمعلومات الإضافية)
     * @return قيمة الفائدة المحسوبة
     */
    Double calculateInterest(Double principal, Integer months, Account account);
    
    /**
     * اسم الاستراتيجية
     */
    String getStrategyName();
    
    /**
     * وصف الاستراتيجية
     */
    String getDescription();
    
    /**
     * معدل الفائدة السنوي
     */
    Double getAnnualInterestRate();
    
    /**
     * أنواع الحسابات المدعومة
     */
    AccountType[] getSupportedAccountTypes();
    
    /**
     * الحد الأدنى للمبلغ
     */
    default Double getMinimumBalance() {
        return 0.0;
    }
    
    /**
     * الحد الأقصى للمبلغ
     */
    default Double getMaximumBalance() {
        return Double.MAX_VALUE;
    }
    
    /**
     * هل الاستراتيجية نشطة؟
     */
    default boolean isActive() {
        return true;
    }
}