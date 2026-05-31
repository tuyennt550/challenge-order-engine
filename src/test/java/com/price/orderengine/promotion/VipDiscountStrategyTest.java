package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.impl.VipDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VipDiscountStrategyTest {
    private final VipDiscountStrategy strategy = new VipDiscountStrategy();

    @Test
    void should_apply_vip_discount() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.VIP_DISCOUNT,
                new BigDecimal("5"),
                true
        );

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .customerType(CustomerType.VIP.toString())
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("200"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(0, new BigDecimal("10").compareTo(result.getDiscount()));
        assertEquals(1, result.getAppliedPromotions().size());
    }

    @Test
    void should_not_apply_if_not_vip() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.VIP_DISCOUNT,
                new BigDecimal("5"),
                true
        );

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .customerType(CustomerType.REGULAR.toString())
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("200"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(BigDecimal.ZERO, result.getDiscount());
        assertTrue(result.getAppliedPromotions().isEmpty());
    }
}
