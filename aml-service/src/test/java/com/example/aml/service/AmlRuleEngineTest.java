package com.example.aml.service;

import com.example.aml.domain.PaymentEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmlRuleEngineTest {

    @Test
    void shouldGenerateAlertWhenAmountExceedsThreshold() {
        AmlRuleEngine engine = new AmlRuleEngine(new BigDecimal("10000"));
        PaymentEvent event = new PaymentEvent("tx-1", "idem", "123", "456", new BigDecimal("15000"), Instant.now());

        assertTrue(engine.evaluate(event).isPresent());
    }

    @Test
    void shouldNotGenerateAlertWhenAmountBelowThreshold() {
        AmlRuleEngine engine = new AmlRuleEngine(new BigDecimal("10000"));
        PaymentEvent event = new PaymentEvent("tx-1", "idem", "123", "456", new BigDecimal("9000"), Instant.now());

        assertFalse(engine.evaluate(event).isPresent());
    }
}
