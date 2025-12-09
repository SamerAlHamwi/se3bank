package com.bank.se3bank.adapters;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "المدفوعات (Adapter)", description = "معالجة المدفوعات باستخدام مزودي دفع خارجيين بشكل مقلد")
public class PaymentController {

    private final PaymentGatewayAdapter paymentGatewayAdapter;

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "معالجة دفع", description = "معالجة دفع باستخدام Stripe أو PayPal (محاكاة ناجحة دائماً)")
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest request) {
        // provider is optional, defaults to Stripe
        PaymentResponse response = paymentGatewayAdapter.processWithGateway("stripe", request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

