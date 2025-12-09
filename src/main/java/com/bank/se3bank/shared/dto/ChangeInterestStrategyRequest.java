package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeInterestStrategyRequest {
    @NotBlank(message = "اسم الاستراتيجية مطلوب")
    private String strategyName;
    
    private String reason;
}