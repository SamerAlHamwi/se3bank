package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * مصنع لإنشاء Decorators باستخدام Factory Pattern
 */
@Component
@Slf4j
public class DecoratorFactory {
    
    /**
     * إنشاء ديكور بناءً على النوع
     */
    public AccountDecorator createDecorator(Account account, AddDecoratorRequest request) {
        log.info("🏭 إنشاء ديكور من النوع: {} للحساب: {}", 
                request.getDecoratorType(), account.getAccountNumber());
        
        return switch (request.getDecoratorType().toUpperCase()) {
            case "OVERDRAFT_PROTECTION" -> new OverdraftProtectionDecorator(
                    account, 
                    request.getOverdraftLimit()
            );
            
            case "INSURANCE" -> new InsuranceDecorator(
                    account,
                    request.getCoverageAmount(),
                    request.getInsuranceType()
            );
            
            case "PREMIUM_SERVICES" -> new PremiumServicesDecorator(
                    account,
                    request.getTierLevel()
            );
            
            default -> throw new IllegalArgumentException(
                    "نوع الديكور غير معروف: " + request.getDecoratorType()
            );
        };
    }
    
    /**
     * تطبيق عدة ديكورات على حساب
     */
    public Account applyMultipleDecorators(Account account, AddDecoratorRequest... requests) {
        Account decoratedAccount = account;
        
        for (AddDecoratorRequest request : requests) {
            decoratedAccount = createDecorator(decoratedAccount, request);
        }
        
        return decoratedAccount;
    }
    
    /**
     * التحقق إذا كان النوع المدخل صحيحاً
     */
    public boolean isValidDecoratorType(String decoratorType) {
        return decoratorType != null && (
                decoratorType.equalsIgnoreCase("OVERDRAFT_PROTECTION") ||
                decoratorType.equalsIgnoreCase("INSURANCE") ||
                decoratorType.equalsIgnoreCase("PREMIUM_SERVICES")
        );
    }
    
    /**
     * الحصول على وصف لأنواع الديكورات المتاحة
     */
    public String getAvailableDecoratorsInfo() {
        return """
               الأنواع المتاحة:
               1. OVERDRAFT_PROTECTION - حماية السحب على المكشوف
               2. INSURANCE - تأمين على الحساب
               3. PREMIUM_SERVICES - خدمات مميزة
               """;
    }
}