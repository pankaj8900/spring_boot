package com.example.payment.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountServiceClient {
    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();
    private final Set<String> inactiveAccounts = ConcurrentHashMap.newKeySet();

    public AccountServiceClient() {
        balances.put("123", new BigDecimal("50000"));
        balances.put("456", new BigDecimal("20000"));
        balances.put("789", new BigDecimal("1000"));
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "unavailable")
    public synchronized void validateAndDebit(String fromAccount, BigDecimal amount) {
        if (inactiveAccounts.contains(fromAccount)) {
            throw new IllegalStateException("Source account inactive");
        }
        BigDecimal current = balances.getOrDefault(fromAccount, BigDecimal.ZERO);
        if (current.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balances.put(fromAccount, current.subtract(amount));
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "unavailable")
    public synchronized void credit(String toAccount, BigDecimal amount) {
        if (inactiveAccounts.contains(toAccount)) {
            throw new IllegalStateException("Destination account inactive");
        }
        balances.merge(toAccount, amount, BigDecimal::add);
    }

    private void unavailable(String account, BigDecimal amount, Throwable throwable) {
        throw new IllegalStateException("Account service unavailable", throwable);
    }

    public synchronized void compensate(String fromAccount, String toAccount, BigDecimal amount) {
        balances.merge(fromAccount, amount, BigDecimal::add);
        balances.merge(toAccount, amount.negate(), BigDecimal::add);
    }
}
