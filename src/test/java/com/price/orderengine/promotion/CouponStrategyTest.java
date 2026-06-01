package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.CustomerType;
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

        PromotionContext ctx = PromotionTestHelper.createContext(
                CustomerType.REGULAR,
                List.of(
                        PromotionTestHelper.item("A100", 100, 2)
                ),
                PromotionTestHelper.coupon("SUMMER10", 10),
                BigDecimal.valueOf(200),
                List.of()
        );

        PromotionResult result = strategy.apply(ctx);

        assertEquals(
                0,
                BigDecimal.valueOf(10)
                        .compareTo(result.getDiscount())
        );
        assertEquals(1, result.getAppliedPromotions().size());
        assertTrue(result.getAppliedPromotions().get(0).getType().contains(PromotionType.COUPON.toString()));
    }
}
