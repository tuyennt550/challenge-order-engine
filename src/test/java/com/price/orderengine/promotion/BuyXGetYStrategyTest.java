package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
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
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.BUY_2_GET_1_FREE,
                new BigDecimal("2"),
                true
        );

        OrderItemRequest item = OrderItemRequest.builder()
                .price(new BigDecimal("100"))
                .quantity(2)
                .build();

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .items(List.of(item))
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("200"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        // 2 items, buy 2 get 1 => 1 free item
        // discount = 100

        assertEquals(new BigDecimal("100"), result.getDiscount());
        assertEquals(1, ctx.getPromotions().size());
    }

    @Test
    void should_not_apply_if_not_enough_quantity() {
        PromotionConfigDTO promo = new PromotionConfigDTO(
                PromotionType.BUY_2_GET_1_FREE,
                new BigDecimal("2"),
                true
        );

        OrderItemRequest item = OrderItemRequest.builder()
                .price(new BigDecimal("100"))
                .quantity(1)
                .build();

        CalculateOrderRequest request = CalculateOrderRequest.builder()
                .items(List.of(item))
                .build();

        PromotionContext ctx = PromotionTestHelper.createContext(
                new BigDecimal("100"),
                List.of(promo),
                request
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(BigDecimal.ZERO, result.getDiscount());
        assertTrue(result.getAppliedPromotions().isEmpty());
    }
}
