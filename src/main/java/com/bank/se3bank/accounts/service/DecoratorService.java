package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.decorators.AccountDecorator;
import com.bank.se3bank.accounts.decorators.DecoratorFactory;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.repository.AccountDecoratorRepository;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecoratorService {
    
    private final AccountDecoratorRepository decoratorRepository;
    private final AccountService accountService;
    private final DecoratorFactory decoratorFactory;
    
    @Transactional
    public AccountDecorator addDecorator(AddDecoratorRequest request) {
        log.info("🎨 Adding decorator {} to account {}", request.getDecoratorType(), request.getAccountId());
        
        Account account = accountService.getAccountById(request.getAccountId());
        
        // Check if this type of decorator already exists and is active
        boolean alreadyHasDecorator = decoratorRepository.findByDecoratedAccountIdAndIsActiveTrue(account.getId())
            .stream()
            .anyMatch(d -> d.getClass().getSimpleName().toUpperCase().contains(request.getDecoratorType().toUpperCase()));

        if (alreadyHasDecorator) {
            throw new IllegalStateException("Account already has an active decorator of type: " + request.getDecoratorType());
        }
        
        AccountDecorator decorator = decoratorFactory.createDecorator(account, request);
        
        AccountDecorator savedDecorator = decoratorRepository.save(decorator);
        
        log.info("✅ Decorator {} added successfully to account {}", savedDecorator.getDecoratorName(), account.getAccountNumber());
        
        return savedDecorator;
    }
    
    @Transactional
    public void removeDecorator(Long decoratorId) {
        AccountDecorator decorator = decoratorRepository.findById(decoratorId)
                .orElseThrow(() -> new IllegalArgumentException("Decorator not found"));
        
        decorator.deactivate();
        decoratorRepository.save(decorator);
        
        log.info("🗑️ Decorator {} has been deactivated.", decorator.getDecoratorName());
    }
    
    @Transactional
    public AccountDecorator activateDecorator(Long decoratorId) {
        AccountDecorator decorator = decoratorRepository.findById(decoratorId)
                .orElseThrow(() -> new IllegalArgumentException("Decorator not found"));
        
        decorator.activate();
        AccountDecorator savedDecorator = decoratorRepository.save(decorator);
        
        log.info("✅ Decorator {} has been activated.", savedDecorator.getDecoratorName());
        
        return savedDecorator;
    }
    
    public List<AccountDecorator> getAccountDecorators(Long accountId) {
        return decoratorRepository.findByDecoratedAccountId(accountId);
    }
    
    public List<AccountDecorator> getActiveDecorators(Long accountId) {
        return decoratorRepository.findByDecoratedAccountIdAndIsActiveTrue(accountId);
    }
    
    @Transactional
    public void applyAllMonthlyFees() {
        List<AccountDecorator> activeDecorators = decoratorRepository.findByIsActiveTrue();
        log.info("💰 Applying monthly fees for {} active decorators.", activeDecorators.size());
        
        activeDecorators.forEach(decorator -> {
            try {
                decorator.applyMonthlyFee();
                decoratorRepository.save(decorator);
            } catch (Exception e) {
                log.error("❌ Error applying fee for decorator {}: {}", decorator.getId(), e.getMessage());
            }
        });
    }
    
    public List<String> getAccountFeatures(Long accountId) {
        // Start with base features
        List<String> features = new java.util.ArrayList<>(List.of("BASIC_BANKING", "ONLINE_BANKING", "MOBILE_BANKING"));
        
        // Add features from active decorators
        List<AccountDecorator> activeDecorators = getActiveDecorators(accountId);
        features.addAll(
            activeDecorators.stream()
                .flatMap(d -> d.getAddedFeatures().stream())
                .distinct()
                .collect(Collectors.toList())
        );
        
        return features;
    }
}
