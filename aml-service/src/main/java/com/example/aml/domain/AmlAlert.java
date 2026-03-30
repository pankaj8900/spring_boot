package com.example.aml.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record AmlAlert(
        String transactionId,
        String reason,
        BigDecimal amount,
        Instant createdAt
) {
}
