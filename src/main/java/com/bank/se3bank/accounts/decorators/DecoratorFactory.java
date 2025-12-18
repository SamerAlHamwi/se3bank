package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import com.bank.se3bank.shared.dto.DecoratorTypeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DecoratorFactory {

    public AccountDecorator createDecorator(Account account, AddDecoratorRequest request) {
        log.info("🏭 Creating decorator of type: {} for account: {}", 
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
                    "Unknown decorator type: " + request.getDecoratorType()
            );
        };
    }

    public boolean isValidDecoratorType(String decoratorType) {
        return decoratorType != null && (
                decoratorType.equalsIgnoreCase("OVERDRAFT_PROTECTION") ||
                decoratorType.equalsIgnoreCase("INSURANCE") ||
                decoratorType.equalsIgnoreCase("PREMIUM_SERVICES")
        );
    }

    public List<DecoratorTypeDTO> getAvailableDecoratorTypes() {
        return List.of(
            new DecoratorTypeDTO("OVERDRAFT_PROTECTION", "Overdraft Protection", "Allows withdrawals beyond the available balance up to a certain limit."),
            new DecoratorTypeDTO("INSURANCE", "Account Insurance", "Provides coverage against theft, fraud, or loss."),
            new DecoratorTypeDTO("PREMIUM_SERVICES", "Premium Services", "Offers exclusive benefits like a personal account manager, discounts, and preferential rates.")
        );
    }
}
