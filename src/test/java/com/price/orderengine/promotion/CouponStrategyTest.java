package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.impl.CouponStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CouponStrategyTest {
    private final CouponStrategy strategy = new CouponStrategy();

    @Test
    void should_apply_coupon_discount() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.COUPON,
                new BigDecimal("10"),
                true
        );

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .couponCode("SUMMER10")
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("100"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(new BigDecimal("10"), result.getDiscount());
        assertEquals(1, result.getAppliedPromotions().size());
        assertTrue(result.getAppliedPromotions().get(0).getType().contains(PromotionType.COUPON.toString()));
    }

    @Test
    void should_apply_even_if_coupon_code_null() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.COUPON,
                new BigDecimal("10"),
                true
        );

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .couponCode(null)
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("100"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(new BigDecimal("10"), result.getDiscount());
    }
}
