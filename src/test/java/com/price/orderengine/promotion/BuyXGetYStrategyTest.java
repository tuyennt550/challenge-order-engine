package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.impl.BuyXGetYStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuyXGetYStrategyTest {
    private final BuyXGetYStrategy strategy = new BuyXGetYStrategy();

    @Test
    void should_apply_buy_2_get_1_free() {
        PromotionConfigDTO promo = PromotionTestHelper.promotion(PromotionType.BUY_2_GET_1_FREE, 2);

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

        // 2 items, buy 2 get 1 => 1 free item
        // discount = 100

        assertEquals(
                0,
                BigDecimal.valueOf(100)
                        .compareTo(result.getDiscount())
        );
        assertEquals(1, ctx.getPromotions().size());
    }

    @Test
    void should_apply_buy_3_get_1_free() {
        PromotionConfigDTO promo = PromotionTestHelper.promotion(PromotionType.BUY_2_GET_1_FREE, 2);

        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 3)
                ),
                null,
                BigDecimal.valueOf(300),
                List.of(promo)
        );

        PromotionResult result = strategy.apply(ctx);

        // 3 items, buy 2 get 1 => 2 free item
        // discount = 100

        assertEquals(
                0,
                BigDecimal.valueOf(100)
                        .compareTo(result.getDiscount())
        );
        assertEquals(1, ctx.getPromotions().size());
    }

    @Test
    void should_apply_buy_4_get_2_free() {
        PromotionConfigDTO promo = PromotionTestHelper.promotion(PromotionType.BUY_2_GET_1_FREE, 2);

        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 4)
                ),
                null,
                BigDecimal.valueOf(400),
                List.of(promo)
        );

        PromotionResult result = strategy.apply(ctx);

        // 4 items, buy 2 get 1 => 2 free item
        // discount = 200

        assertEquals(
                0,
                BigDecimal.valueOf(200)
                        .compareTo(result.getDiscount())
        );
        assertEquals(1, ctx.getPromotions().size());
    }

    @Test
    void should_not_apply_if_not_enough_quantity() {
        PromotionConfigDTO promo = PromotionTestHelper.promotion(PromotionType.BUY_2_GET_1_FREE, 2);

        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 1)
                ),
                null,
                BigDecimal.valueOf(200),
                List.of(promo)
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(BigDecimal.ZERO, result.getDiscount());
        assertTrue(result.getAppliedPromotions().isEmpty());
    }
}
