package com.example.payment.api;

import com.example.payment.domain.PaymentTransaction;
import com.example.payment.service.PaymentProcessor;
import com.example.payment.service.PaymentStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentProcessor paymentProcessor;
    private final PaymentStore paymentStore;

    public PaymentController(PaymentProcessor paymentProcessor, PaymentStore paymentStore) {
        this.paymentProcessor = paymentProcessor;
        this.paymentStore = paymentStore;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        String key = idempotencyKey == null || idempotencyKey.isBlank() ? UUID.randomUUID().toString() : idempotencyKey;
        PaymentTransaction tx = paymentProcessor.process(request, key);
        PaymentResponse response = new PaymentResponse(
                tx.transactionId(), tx.idempotencyKey(), tx.fromAccount(), tx.toAccount(), tx.amount(), tx.status(), tx.processedAt());

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public Collection<PaymentTransaction> listPayments() {
        return paymentStore.allPayments();
    }

    @GetMapping("/audit")
    public List<String> auditLogs() {
        return paymentStore.auditLogs();
    }
}
