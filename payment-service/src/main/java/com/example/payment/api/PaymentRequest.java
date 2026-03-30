package com.example.payment.api;

import java.math.BigDecimal;

public record PaymentRequest(String fromAccount, String toAccount, BigDecimal amount) {
}
