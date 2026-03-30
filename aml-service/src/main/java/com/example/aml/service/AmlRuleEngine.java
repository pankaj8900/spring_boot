package com.example.aml.service;

import com.example.aml.domain.AmlAlert;
import com.example.aml.domain.PaymentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class AmlRuleEngine {
    private final java.math.BigDecimal threshold;

    public AmlRuleEngine(@Value("${aml.threshold:10000}") java.math.BigDecimal threshold) {
        this.threshold = threshold;
    }

    public Optional<AmlAlert> evaluate(PaymentEvent event) {
        if (event.amount() != null && event.amount().compareTo(threshold) > 0) {
            return Optional.of(new AmlAlert(
                    event.transactionId(),
                    "Amount exceeds threshold " + threshold,
                    event.amount(),
                    Instant.now()));
        }
        return Optional.empty();
    }
}
