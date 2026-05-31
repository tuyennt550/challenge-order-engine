package com.price.orderengine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @Column(length = 50, nullable = false, updatable = false)
    private String code;

    @Column(name = "discount_amount",
            nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public boolean isValid() {
        return active && expiryDate.isAfter(Instant.now());
    }
}
