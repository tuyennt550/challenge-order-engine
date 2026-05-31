package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.impl.PercentageDiscountStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PercentageDiscountStrategyTest {
    private final PercentageDiscountStrategy strategy = new PercentageDiscountStrategy();

    @Test
    void should_apply_percentage_discount() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.PERCENTAGE_DISCOUNT,
                new BigDecimal("10"),
                true
        );

        CalculateOrderRequest request = CalculateOrderRequest.builder().build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("100"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(0, new BigDecimal("10").compareTo(result.getDiscount()));
        assertEquals(1, result.getAppliedPromotions().size());
    }

    @Test
    void should_do_nothing_when_no_promotion() {
        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("100"),
                List.of(),
                CalculateOrderRequest.builder().build()
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(BigDecimal.ZERO, result.getDiscount());
        assertTrue(result.getAppliedPromotions().isEmpty());
    }
}
