package com.bank.se3bank.adapters;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentGatewayAdapterTest {

    private final StripeAdapter stripeAdapter = new StripeAdapter();
    private final PayPalAdapter payPalAdapter = new PayPalAdapter();
    private final PaymentGatewayAdapter adapter = new PaymentGatewayAdapter(stripeAdapter, payPalAdapter);

    @Test
    void processWithStripe_success() {
        PaymentRequest request = PaymentRequest.builder()
                .accountNumber("ACC-1")
                .recipient("Store")
                .amount(50.0)
                .currency("USD")
                .build();

        PaymentResponse response = adapter.processWithGateway("stripe", request);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getTransactionId()).startsWith("stripe_");
    }

    @Test
    void processWithPayPal_success() {
        PaymentRequest request = PaymentRequest.builder()
                .accountNumber("ACC-2")
                .recipient("Merchant")
                .amount(75.0)
                .currency("USD")
                .build();

        PaymentResponse response = adapter.processWithGateway("paypal", request);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getTransactionId()).startsWith("paypal_");
    }
}

