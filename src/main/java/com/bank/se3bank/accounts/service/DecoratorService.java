package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.decorators.*;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.repository.AccountDecoratorRepository;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecoratorService {
    
    private final AccountDecoratorRepository decoratorRepository;
    private final AccountService accountService;
    private final DecoratorFactory decoratorFactory;
    
    /**
     * إضافة ديكور لحساب
     */
    @SuppressWarnings("null")
    @Transactional
    public AccountDecorator addDecorator(AddDecoratorRequest request) {
        log.info("🎨 إضافة ديكور {} للحساب {}", 
                request.getDecoratorType(), request.getAccountId());
        
        Account account = accountService.getAccountById(request.getAccountId());
        
        // التحقق إذا كان الحساب يحتوي بالفعل على هذا النوع من الديكورات
        if (hasDecorator(account, request.getDecoratorType())) {
            throw new IllegalStateException(
                    "الحساب يحتوي بالفعل على ديكور من النوع: " + request.getDecoratorType()
            );
        }
        
        // إنشاء الديكور باستخدام Factory
        AccountDecorator decorator = decoratorFactory.createDecorator(account, request);
        
        // حفظ الديكور في قاعدة البيانات
        AccountDecorator savedDecorator = decoratorRepository.save(decorator);
        
        log.info("✅ تم إضافة ديكور {} للحساب {} بنجاح",
                savedDecorator.getDecoratorName(), account.getAccountNumber());
        
        return savedDecorator;
    }
    
    /**
     * إزالة ديكور من حساب
     */
    @Transactional
    public void removeDecorator(Long decoratorId) {
        @SuppressWarnings("null")
        AccountDecorator decorator = decoratorRepository.findById(decoratorId)
                .orElseThrow(() -> new IllegalArgumentException("الديكور غير موجود"));
        
        decorator.deactivate();
        decoratorRepository.save(decorator);
        
        log.info("🗑️ تم تعطيل الديكور: {}", decorator.getDecoratorName());
    }
    
    /**
     * تفعيل ديكور
     */
    @SuppressWarnings("null")
    @Transactional
    public AccountDecorator activateDecorator(Long decoratorId) {
        AccountDecorator decorator = decoratorRepository.findById(decoratorId)
                .orElseThrow(() -> new IllegalArgumentException("الديكور غير موجود"));
        
        decorator.activate();
        AccountDecorator savedDecorator = decoratorRepository.save(decorator);
        
        log.info("✅ تم تفعيل الديكور: {}", savedDecorator.getDecoratorName());
        
        return savedDecorator;
    }
    
    /**
     * الحصول على جميع ديكورات حساب
     */
    public List<AccountDecorator> getAccountDecorators(Long accountId) {
        return decoratorRepository.findByDecoratedAccountId(accountId);
    }
    
    /**
     * الحصول على ديكورات نشطة فقط
     */
    public List<AccountDecorator> getActiveDecorators(Long accountId) {
        return decoratorRepository.findByDecoratedAccountIdAndIsActiveTrue(accountId);
    }
    
    /**
     * تطبيق جميع الرسوم الشهرية للديكورات
     */
    @Transactional
    public void applyAllMonthlyFees() {
        List<AccountDecorator> activeDecorators = decoratorRepository.findByIsActiveTrue();
        
        log.info("💰 تطبيق الرسوم الشهرية لـ {} ديكور", activeDecorators.size());
        
        activeDecorators.forEach(decorator -> {
            try {
                decorator.applyMonthlyFee();
                decoratorRepository.save(decorator);
            } catch (Exception e) {
                log.error("❌ خطأ في تطبيق الرسوم للديكور {}: {}", 
                        decorator.getId(), e.getMessage());
            }
        });
    }
    
    /**
     * التحقق إذا كان الحساب يحتوي على ديكور معين
     */
    public boolean hasDecorator(Account account, String decoratorType) {
        if (account instanceof AccountDecorator) {
            return ((AccountDecorator) account).hasDecorator(decoratorType);
        }
        
        // التحقق في قاعدة البيانات
        return !decoratorRepository
                .findByDecoratedAccountIdAndDecoratorNameContainingIgnoreCase(
                        account.getId(), decoratorType)
                .isEmpty();
    }
    
    /**
     * الحصول على الحساب مع جميع ديكوراته
     */
    public Account getAccountWithDecorators(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        List<AccountDecorator> decorators = getActiveDecorators(accountId);
        
        // تطبيق الديكورات على الحساب
        Account decoratedAccount = account;
        for (AccountDecorator decorator : decorators) {
            // Note: في تطبيق حقيقي، قد نحتاج إلى طريقة أفضل لربط الديكورات
            decorator.setDecoratedAccount(decoratedAccount);
            decoratedAccount = decorator;
        }
        
        return decoratedAccount;
    }
    
    /**
     * الحصول على ميزات الحساب (الأصلية + المضافة)
     */
    public List<String> getAccountFeatures(Long accountId) {
        Account account = getAccountWithDecorators(accountId);
        List<String> features = new java.util.ArrayList<>();
        
        if (account instanceof AccountDecorator) {
            features.addAll(((AccountDecorator) account).getAddedFeatures());
        }
        
        // إضافة الميزات الأساسية
        features.add("BASIC_BANKING");
        features.add("ONLINE_BANKING");
        features.add("MOBILE_BANKING");
        
        return features;
    }
    
    /**
     * تحديث معاملات الديكور
     */
    @SuppressWarnings("null")
    @Transactional
    public AccountDecorator updateDecorator(Long decoratorId, AddDecoratorRequest request) {
        AccountDecorator decorator = decoratorRepository.findById(decoratorId)
                .orElseThrow(() -> new IllegalArgumentException("الديكور غير موجود"));
        
        // تحديث الخصائص بناءً على النوع
        if (decorator instanceof OverdraftProtectionDecorator overdraftDecorator) {
            if (request.getOverdraftLimit() != null) {
                overdraftDecorator.setOverdraftLimit(request.getOverdraftLimit());
            }
        } else if (decorator instanceof InsuranceDecorator insuranceDecorator) {
            if (request.getCoverageAmount() != null) {
                insuranceDecorator.setCoverageAmount(request.getCoverageAmount());
            }
        } else if (decorator instanceof PremiumServicesDecorator premiumDecorator) {
            if (request.getTierLevel() != null) {
                premiumDecorator.setTierLevel(request.getTierLevel());
            }
        }
        
        if (request.getDescription() != null) {
            decorator.setDescription(request.getDescription());
        }
        
        return decoratorRepository.save(decorator);
    }
}