# Order Pricing & Promotion Engine

## 1. Challenge Chosen and Why

I chose the **Order Pricing & Promotion Engine** challenge because it combines several important backend engineering concerns:

- Domain modeling
- Pricing and promotion calculation
- Database design
- Extensible architecture
- Testing and maintainability

Pricing systems evolve over time as new promotion types are introduced, making this challenge a good opportunity to demonstrate clean architecture, SOLID principles, and extensible design patterns.

---

# 2. Architecture Overview

```text
Controller
    ↓
Service
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
|---------|---------|
| Controller | Handle HTTP requests/responses |
| Service | Business orchestration |
| Promotion Processor | Execute promotion pipeline |
| Promotion Strategies | Individual pricing rules |
| Repository | Data access |
| Entity | Database mapping |

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

Implementations:

- PercentageDiscountStrategy
- VipDiscountStrategy
- CouponStrategy
- BuyXGetYStrategy

### Location

`promotion/impl/*`

### Why

New promotion types can be added without modifying existing implementations.

---

## Chain of Responsibility Pattern

Promotion execution is implemented as a configurable promotion pipeline.

Each promotion handler wraps a strategy and delegates processing to the next handler.
```text
Percentage Discount
        ↓
VIP Discount
        ↓
Coupon
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

## orders

Stores order-level information.

| Column | Purpose |
|----------|----------|
| id | Order identifier |
| customer_type | Customer classification |
| subtotal | Total before discounts |
| total_discount | Total discount |
| final_price | Final payable amount |
| created_at | Creation timestamp |

---

## order_items

Stores purchased products.

| Column | Purpose |
|----------|----------|
| id | Item identifier |
| order_id | Parent order |
| sku | Product SKU |
| price | Unit price |
| quantity | Quantity ordered |

Relationship:

```text
Order 1 ---- * OrderItem
```

---

## coupons

Stores coupon definitions.

| Column          | Purpose |
|-----------------|----------|
| code            | Coupon code |
| discount_amount | Discount value |
| active          | Active flag |
| expiry date     | End date |

---

## Design Choices

### UUID Primary Keys

Benefits:

- Globally unique
- Better support for distributed systems
- Avoid predictable IDs

### BigDecimal for Money

Database:

```sql
NUMERIC(19,2)
```

Java:

```java
BigDecimal
```

Prevents floating-point precision errors.

### Liquibase

Schema changes are managed using Liquibase migrations for versioned and repeatable deployments.

---

# 6. How to Run the System

## Prerequisites

- Java 17
- Maven 3.5.3
- Docker
- Docker Compose

## Run Application

```bash
docker compose up --build
```

Application:

```text
http://localhost:8080
```

---

# 7. How to Run the Tests

Run unit tests:

```bash
mvn test
```

Run integration tests:

```bash
mvn verify
```

Coverage includes:

- Promotion strategies
- Order pricing service
- Controller layer
- Persistence layer
- Integration scenarios

---

# 8. Trade-offs and Future Improvements

## Fixed Promotion Order

Current implementation uses PromotionType ordering.

Pros:

- Predictable
- Easy to test

Cons:

- Less flexible

Future improvement:

- Database-driven priority
- Configuration-based ordering

---

## In-Memory Promotion Processing

Pros:

- Simple implementation
- Easy debugging

Cons:

- Less suitable for complex business rules

Future improvement:

- Dedicated rule engine
- Dynamic rule configuration

---

## Synchronous Processing

Pros:

- Simpler architecture

Cons:

- Increased latency under heavy load

Future improvement:

- Event-driven architecture
- Async processing

---

# 9. What Would Break at Scale and How to Fix It

## Database Bottleneck

Potential issues:

- High write volume
- Query contention

Solutions:

- Read replicas
- Query optimization
- Table partitioning
- Connection pool tuning

---

## Promotion Pipeline Growth

Potential issue:

- Increasing number of promotion rules increases processing time

Solutions:

- Cache promotion metadata
- Rule indexing
- Promotion pre-calculation

---

## Concurrent Orders

Potential issue:

- Inventory inconsistencies

Solutions:

- Optimistic locking
- Pessimistic locking
- Distributed locking when required

---

## Single Instance Deployment

Potential issue:

- Application becomes a bottleneck

Solutions:

- Horizontal scaling
- Load balancing
- Kubernetes deployment

---

# Technologies Used

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
