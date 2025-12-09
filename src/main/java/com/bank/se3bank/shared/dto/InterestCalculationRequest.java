package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterestCalculationRequest {
    @NotNull
    private Double principal;
    
    @NotNull
    @Min(1)
    private Integer months;
    
    private String strategyName;
}