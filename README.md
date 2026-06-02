# Order Pricing & Promotion Engine

# 1. Challenge Chosen and Why

I chose the **Order Pricing & Promotion Engine** challenge because it combines several important backend engineering concerns:

- Domain modeling
- Pricing and promotion calculation
- Database design
- Extensible architecture
- Concurrency and consistency handling
- Testing and maintainability

Pricing systems evolve over time as new promotion types are introduced, making this challenge a good opportunity to demonstrate clean architecture, SOLID principles, and extensible design patterns.

---

# 2. Architecture Overview

```text
Controller
    ↓
Service (OrderPricingService)
    ↓
CouponService (validation + atomic reservation)
    ↓
Promotion Engine
    ↓
Promotion Chain
    ↓
Promotion Strategies
    ↓
Repository
    ↓
Database
```

### Responsibilities

| Layer | Responsibility |
|------|---------------|
| Controller | HTTP request/response |
| Service | Business orchestration |
| CouponService | Coupon validation + concurrency-safe redemption |
| Promotion Engine | Promotion execution pipeline |
| Strategies | Individual promotion rules |
| Repository | Data access |
| Entity | Persistence model |

---

# 3. Design Patterns Used

## Strategy Pattern

Each promotion rule is implemented as a separate strategy.

```java
public interface PromotionStrategy {
    PromotionType getType();
    PromotionResult apply(PromotionContext context);
}
```

Examples:
- PercentageDiscountStrategy
- VipDiscountStrategy
- CouponStrategy
- BuyXGetYStrategy

### Why

Enables Open/Closed principle: new promotions can be added without modifying existing logic.

---

## Chain of Responsibility Pattern

Promotion execution is implemented as a configurable pipeline.

```text
Percentage Discount
        ↓
VIP Discount
        ↓
Coupon Discount
        ↓
Buy X Get Y
```

Handler contract:
```java
public interface PromotionHandler {
    String getType();
    void setNext(PromotionHandler next);
    PromotionResult handle(PromotionContext context);
}
```

Chain construction:

```java
PromotionChainBuilder
```

Chain execution:

```java
PromotionEngine
```

Benefits:

- True promotion pipeline
- Flexible execution order
- Easy insertion of new promotion stages

---

### Location

`promotion`

### Why

Allows multiple independent promotion rules to be combined while keeping them loosely coupled.

# Configurable Promotion Pipeline

Promotion order is externally configured.

Example:

```yaml
promotion:
  chain:
    order:
      - PERCENTAGE_DISCOUNT
      - VIP_DISCOUNT
      - COUPON
      - BUY_2_GET_1_FREE
```

The chain is built dynamically during application startup.

This allows promotion ordering changes without code modifications.
---

# 4. SOLID Principles

## Single Responsibility Principle (SRP)

Examples:

- OrderController → HTTP handling
- OrderService → business orchestration
- PromotionProcessor → promotion execution
- PercentageDiscountStrategy → Percentage discount logic

---

## Open/Closed Principle (OCP)

New promotions can be introduced by implementing:

```java
PromotionStrategy
```

without modifying existing code.

---

## Liskov Substitution Principle (LSP)

All implementations can be used interchangeably through:

```java
PromotionStrategy
```

---

## Interface Segregation Principle (ISP)

The strategy interface contains only behavior required by promotion handlers.

```java
public interface PromotionStrategy {
    PromotionType getType();
    PromotionResult apply(PromotionContext context);
}
```

---

## Dependency Inversion Principle (DIP)

The processor depends on abstractions instead of concrete implementations.

```java
private final List<PromotionStrategy> strategies;
```

Spring injects implementations automatically.

---

# 5. Database Design Decisions

## coupons (CONCURRENCY-SAFE DESIGN)

| Column | Purpose |
|--------|--------|
| code | primary key |
| discount_amount | fixed discount |
| active | enable/disable |
| expiry_date | expiration |
| usage_limit | max allowed redemptions |
| used_count | current usage |
| version | optimistic locking (metadata updates only) |

---

## Key Concurrency Design

### Coupon usage is protected using ATOMIC UPDATE

```sql
UPDATE coupons
SET used_count = used_count + 1
WHERE code = ?
  AND active = true
  AND expiry_date > CURRENT_TIMESTAMP
  AND (usage_limit IS NULL OR used_count < usage_limit);
```

✔ Ensures:
- No double redemption
- Multi-instance safety
- No race condition

---

## orders / order_items

Standard normalized design:
- Order → OrderItems (1:N)

---

## Optimistic Locking (@Version)

Used for **administrative updates only (NOT coupon usage)**.

It protects against:
- concurrent promotion edits
- lost updates in configuration changes

---

## Coupon Concurrency Strategy

### Step 1 — Validate
```java
couponService.validateCoupon(code);
```

### Step 2 — Reserve atomically
```java
couponService.reserveCoupon(code);
```

---

## 7. How to Run the System

```bash
docker compose up --build
```

OpenAPI documentation is available at http://localhost:8080/swagger-ui/index.html.

Order Calculate:
```
curl -X POST http://localhost:8080/api/v1/orders/calculate \
  -H "Content-Type: application/json" \
  -d '{"customerType":"VIP","couponCode":"SUMMER10","items":[{"sku":"A100","price":100,"quantity":2},{"sku":"B200","price":50,"quantity":1}]}'
```
Get Promotion:
```
curl http://localhost:8080/api/v1/promotions
```
Create a Promotion
```
curl -X POST http://localhost:8080/api/v1/promotions \
  -H "Content-Type: application/json" \
  -d '{"type":"PERCENTAGE_DISCOUNT","value":10,"active":true}'
```
Get Products
```
curl http://localhost:8080/api/v1/products
```
---

## 8. How to Run Tests

```bash
mvn test
```

---

## 9. Trade-offs and What Happens at Scale

## Coupon Concurrency
- Atomic DB update ensures safe redemption
- Slight DB write overhead under high load

## Promotion Engine
- Linear execution cost based on number of rules

## Optimistic Locking
- May cause retry under concurrent admin updates

## Scaling Strategy
- Stateless services
- Horizontal scaling supported

---

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Liquibase
- JUnit 5
- Mockito
- Testcontainers
- Docker
- Maven
