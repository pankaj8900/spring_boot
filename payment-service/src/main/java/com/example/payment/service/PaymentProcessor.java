package com.example.payment.service;

import com.example.payment.api.PaymentRequest;
import com.example.payment.domain.PaymentEvent;
import com.example.payment.domain.PaymentTransaction;
import com.example.payment.kafka.PaymentEventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentProcessor {
    private final AccountServiceClient accountServiceClient;
    private final PaymentStore paymentStore;
    private final PaymentEventProducer producer;

    public PaymentProcessor(AccountServiceClient accountServiceClient,
                            PaymentStore paymentStore,
                            PaymentEventProducer producer) {
        this.accountServiceClient = accountServiceClient;
        this.paymentStore = paymentStore;
        this.producer = producer;
    }

    public synchronized PaymentTransaction process(PaymentRequest request, String idempotencyKey) {
        return paymentStore.findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> executePayment(request, idempotencyKey));
    }

    private PaymentTransaction executePayment(PaymentRequest request, String idempotencyKey) {
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String txId = UUID.randomUUID().toString();

        accountServiceClient.validateAndDebit(request.fromAccount(), request.amount());
        try {
            accountServiceClient.credit(request.toAccount(), request.amount());
        } catch (RuntimeException e) {
            accountServiceClient.compensate(request.fromAccount(), request.toAccount(), request.amount());
            throw e;
        }

        PaymentTransaction transaction = new PaymentTransaction(
                txId,
                idempotencyKey,
                request.fromAccount(),
                request.toAccount(),
                request.amount(),
                "SUCCESS",
                Instant.now()
        );
        paymentStore.save(transaction);
        producer.publish(new PaymentEvent(
                transaction.transactionId(),
                transaction.idempotencyKey(),
                transaction.fromAccount(),
                transaction.toAccount(),
                transaction.amount(),
                transaction.processedAt()));
        return transaction;
    }
}
