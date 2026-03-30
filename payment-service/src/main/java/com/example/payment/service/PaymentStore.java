package com.example.payment.service;

import com.example.payment.domain.PaymentTransaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentStore {
    private final Map<String, PaymentTransaction> byIdempotencyKey = new ConcurrentHashMap<>();
    private final List<String> auditLogs = new ArrayList<>();

    public Optional<PaymentTransaction> findByIdempotencyKey(String key) {
        return Optional.ofNullable(byIdempotencyKey.get(key));
    }

    public void save(PaymentTransaction transaction) {
        byIdempotencyKey.put(transaction.idempotencyKey(), transaction);
        synchronized (auditLogs) {
            auditLogs.add("PAYMENT_PROCESSED " + transaction.transactionId());
        }
    }

    public Collection<PaymentTransaction> allPayments() {
        return byIdempotencyKey.values();
    }

    public List<String> auditLogs() {
        synchronized (auditLogs) {
            return List.copyOf(auditLogs);
        }
    }
}
