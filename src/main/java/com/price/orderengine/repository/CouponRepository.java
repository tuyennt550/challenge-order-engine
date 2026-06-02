package com.price.orderengine.repository;

import com.price.orderengine.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByCodeAndActiveTrue(String code);

    @Modifying
    @Query("""
        UPDATE Coupon c
        SET c.usedCount = c.usedCount + 1
        WHERE c.code = :code
          AND c.active = true
          AND c.expiryDate > CURRENT_TIMESTAMP
          AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)
        """)
    int redeemCoupon(@Param("code") String code);
}
