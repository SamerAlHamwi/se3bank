package com.bank.se3bank.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayAdapter {

    private final StripeAdapter stripeAdapter;
    private final PayPalAdapter payPalAdapter;

    public PaymentResponse processWithGateway(String provider, PaymentRequest request) {
        if ("paypal".equalsIgnoreCase(provider)) {
            return payPalAdapter.processPayment(request);
        }
        // default to stripe for unknown providers
        return stripeAdapter.processPayment(request);
    }
}
