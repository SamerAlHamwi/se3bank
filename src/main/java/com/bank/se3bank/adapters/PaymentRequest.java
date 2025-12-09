package com.bank.se3bank.adapters;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    @NotBlank
    private String accountNumber;

    @NotBlank
    private String recipient;

    @NotNull
    @Min(1)
    private Double amount;

    private String currency;
    private String description;
}

