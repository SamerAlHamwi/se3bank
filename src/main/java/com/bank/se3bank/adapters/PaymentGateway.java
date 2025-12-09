package com.bank.se3bank.adapters;

public interface PaymentGateway {
    PaymentResponse processPayment(PaymentRequest request);
}

