package com.example.payment.api;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String transactionId,
        String idempotencyKey,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String status,
        Instant processedAt
) {
}
