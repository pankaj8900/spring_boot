package com.example.payment.service;

import com.example.payment.api.PaymentRequest;
import com.example.payment.domain.PaymentEvent;
import com.example.payment.domain.PaymentTransaction;
import com.example.payment.kafka.PaymentEventProducer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentProcessorTest {

    @Test
    void shouldReturnSameTransactionForDuplicateIdempotencyKey() {
        AccountServiceClient accountServiceClient = new AccountServiceClient();
        PaymentStore store = new PaymentStore();
        PaymentEventProducer producer = new PaymentEventProducerStub();
        PaymentProcessor processor = new PaymentProcessor(accountServiceClient, store, producer);

        PaymentRequest request = new PaymentRequest("123", "456", new BigDecimal("100"));

        PaymentTransaction first = processor.process(request, "idem-1");
        PaymentTransaction second = processor.process(request, "idem-1");

        assertEquals(first.transactionId(), second.transactionId());
        assertEquals(1, store.allPayments().size());
    }

    static class PaymentEventProducerStub extends PaymentEventProducer {
        PaymentEventProducerStub() {
            super(null);
        }

        @Override
        public void publish(PaymentEvent event) {
            // no-op
        }
    }
}
