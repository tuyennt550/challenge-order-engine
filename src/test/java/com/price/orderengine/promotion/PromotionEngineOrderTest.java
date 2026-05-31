package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.enums.PromotionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PromotionEngineOrderTest {
    @Test
    void should_execute_strategies_in_enum_order() {

        List<String> executionOrder = new ArrayList<>();

        PromotionStrategy vip = new PromotionStrategy() {
            @Override
            public PromotionType getType() {
                return PromotionType.VIP_DISCOUNT;
            }

            @Override
            public PromotionResult apply(PromotionContext context) {
                executionOrder.add("VIP");
                return PromotionResult.empty();
            }
        };

        PromotionStrategy percent = new PromotionStrategy() {
            @Override
            public PromotionType getType() {
                return PromotionType.PERCENTAGE_DISCOUNT;
            }

            @Override
            public PromotionResult apply(PromotionContext context) {
                executionOrder.add("PERCENT");
                return PromotionResult.empty();
            }
        };

        PromotionStrategy coupon = new PromotionStrategy() {
            @Override
            public PromotionType getType() {
                return PromotionType.COUPON;
            }

            @Override
            public PromotionResult apply(PromotionContext context) {
                executionOrder.add("COUPON");
                return PromotionResult.empty();
            }
        };

        PromotionStrategy buyX = new PromotionStrategy() {
            @Override
            public PromotionType getType() {
                return PromotionType.BUY_2_GET_1_FREE;
            }

            @Override
            public PromotionResult apply(PromotionContext context) {
                executionOrder.add("BUY_X");
                return PromotionResult.empty();
            }
        };

        PromotionEngine engine = new PromotionEngine(
                List.of(coupon, vip, buyX, percent) // intentionally shuffled
        );

        PromotionContext ctx = PromotionContext.builder()
                .request(CalculateOrderRequest.builder().build())
                .promotions(List.of())
                .subtotal(new BigDecimal("100"))
                .build();

        engine.execute(ctx);

        assertEquals(
                List.of(
                        "PERCENT",   // PERCENTAGE_DISCOUNT (ordinal 0)
                        "VIP",       // VIP_DISCOUNT
                        "COUPON",    // COUPON
                        "BUY_X"      // BUY_2_GET_1_FREE
                ),
                executionOrder
        );
    }
}
