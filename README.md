# Real-Time Payment Processing + AML (Spring Boot + Kafka)

This repository contains a minimal microservices implementation for real-time payments with AML screening.

## Services

- **payment-service** (`:8081`)
  - `POST /payments` accepts payment requests.
  - Uses idempotency key (`Idempotency-Key` header) to deduplicate duplicate requests.
  - Validates/debits/credits accounts and publishes `payments` events to Kafka.
  - Exposes `GET /payments` and `GET /payments/audit`.

- **aml-service** (`:8082`)
  - Consumes `payments` events from Kafka.
  - Applies AML rule: `amount > aml.threshold` (default `10000`).
  - Publishes suspicious transactions to `aml-alerts` topic.
  - Exposes `GET /aml/alerts`.

## Key Design Choices

- **Event-driven architecture:** payment processing and AML detection are decoupled through Kafka.
- **Idempotency:** duplicate client submissions return the same processed transaction for a given key.
- **Compensation (Saga-like):** if credit fails after debit, a compensating update restores balances.
- **Circuit breaker:** Resilience4j wraps account operations to protect from cascading failures.
- **Scalability:** Kafka consumer groups and partitions support horizontal scaling.
- **Observability:** Spring Actuator endpoints enabled (`health`, `metrics`, `prometheus`).

## Topics

- `payments`
- `aml-alerts`
- `notifications` (reserved for future notification-service)

## Run

```bash
mvn test
mvn -pl payment-service spring-boot:run
mvn -pl aml-service spring-boot:run
```

## Sample API

```http
POST /payments
Idempotency-Key: 4a75a1f5-11fd-47d2-8b37-cfb8dc742ed4
Content-Type: application/json

{
  "fromAccount": "123",
  "toAccount": "456",
  "amount": 10000
}
```

Response:

```json
{
  "transactionId": "...",
  "idempotencyKey": "4a75a1f5-11fd-47d2-8b37-cfb8dc742ed4",
  "fromAccount": "123",
  "toAccount": "456",
  "amount": 10000,
  "status": "SUCCESS",
  "processedAt": "2026-03-30T00:00:00Z"
}
```
