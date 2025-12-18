package com.bank.se3bank.shared.dto;

import com.bank.se3bank.accounts.decorators.AccountDecorator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecoratorResponse {
    private Long id;
    private String decoratorName;
    private String decoratorType;
    private String description;
    private Double monthlyFee;
    private Boolean isActive;
    private LocalDateTime activatedAt;
    private LocalDateTime deactivatedAt;
    private String accountNumber;
    private String accountType;

    public static DecoratorResponse fromEntity(AccountDecorator decorator) {
        return DecoratorResponse.builder()
                .id(decorator.getId())
                .decoratorName(decorator.getDecoratorName())
                .decoratorType(decorator.getClass().getSimpleName()) // e.g. "InsuranceDecorator"
                .description(decorator.getDescription())
                .monthlyFee(decorator.getMonthlyFee())
                .isActive(decorator.getIsActive())
                .activatedAt(decorator.getActivatedAt())
                .deactivatedAt(decorator.getDeactivatedAt())
                .accountNumber(decorator.getOriginalAccountNumber())
                .accountType(decorator.getAccountType()) // This now returns a String directly
                .build();
    }
}
