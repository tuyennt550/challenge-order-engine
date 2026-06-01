package com.price.orderengine.promotion;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.impl.BuyXGetYStrategy;
import com.price.orderengine.promotion.impl.CouponStrategy;
import com.price.orderengine.promotion.impl.PercentageDiscountStrategy;
import com.price.orderengine.promotion.impl.VipDiscountStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PromotionEngineOrderTest {
    @Test
    void shouldExecuteEntirePipeline() {

        PromotionHandler percentage =
                new StrategyPromotionHandler(
                        new PercentageDiscountStrategy());

        PromotionHandler vip =
                new StrategyPromotionHandler(
                        new VipDiscountStrategy());

        PromotionHandler coupon =
                new StrategyPromotionHandler(
                        new CouponStrategy());

        PromotionHandler buyXGetY =
                new StrategyPromotionHandler(
                        new BuyXGetYStrategy());

        percentage.setNext(vip);
        vip.setNext(coupon);
        coupon.setNext(buyXGetY);

        Coupon couponEntity = Coupon.builder()
                .code("SUMMER10")
                .discountAmount(BigDecimal.valueOf(10))
                .build();

        PromotionContext context = PromotionContext.builder()
                .customerType(CustomerType.VIP)
                .subtotal(BigDecimal.valueOf(250))
                .coupon(couponEntity)
                .items(List.of(
                        OrderItemModel.builder()
                                .sku("A100")
                                .price(BigDecimal.valueOf(100))
                                .quantity(2)
                                .build()
                ))
                .promotions(List.of(
                        new PromotionConfigDTO(
                                PromotionType.PERCENTAGE_DISCOUNT,
                                BigDecimal.TEN,
                                true
                        ),
                        new PromotionConfigDTO(
                                PromotionType.VIP_DISCOUNT,
                                BigDecimal.valueOf(5),
                                true
                        ),
                        new PromotionConfigDTO(
                                PromotionType.BUY_2_GET_1_FREE,
                                BigDecimal.valueOf(2),
                                true
                        )
                ))
                .build();

        PromotionResult result = percentage.handle(context);

        assertEquals(4, result.getAppliedPromotions().size());

        assertEquals(
                0,
                BigDecimal.valueOf(147.5)
                        .compareTo(result.getDiscount())
        );
    }
}
