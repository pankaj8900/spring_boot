package com.example.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentTransaction(
        String transactionId,
        String idempotencyKey,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String status,
        Instant processedAt
) {
}
