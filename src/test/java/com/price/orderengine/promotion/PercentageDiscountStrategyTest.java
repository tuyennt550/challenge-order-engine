package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.CustomerType;
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
        PromotionConfigDTO promo = PromotionTestHelper.promotion(PromotionType.PERCENTAGE_DISCOUNT, 10);

        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 2)
                ),
                null,
                BigDecimal.valueOf(200),
                List.of(promo)
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(0, new BigDecimal("20").compareTo(result.getDiscount()));
        assertEquals(1, result.getAppliedPromotions().size());
    }

    @Test
    void should_do_nothing_when_no_promotion() {
        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 2)
                ),
                null,
                BigDecimal.valueOf(200),
                List.of()
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(BigDecimal.ZERO, result.getDiscount());
        assertTrue(result.getAppliedPromotions().isEmpty());
    }
}
