package com.bank.se3bank.adapters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PayPalAdapter implements PaymentGateway {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("🔌 Mock PayPal payment for account {} amount {}", request.getAccountNumber(), request.getAmount());
        return PaymentResponse.builder()
                .status("SUCCESS")
                .transactionId("paypal_" + UUID.randomUUID())
                .message("Mock payment processed successfully via PayPal")
                .build();
    }
}

