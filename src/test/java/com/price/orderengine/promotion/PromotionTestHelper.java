package com.price.orderengine.promotion;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.enums.PromotionType;

import java.math.BigDecimal;
import java.util.List;

public class PromotionTestHelper {
    public static PromotionContext createContext(
            CustomerType customerType,
            List<OrderItemModel> items,
            Coupon coupon,
            BigDecimal subtotal,
            List<PromotionConfigDTO> promotions
    ) {
        return PromotionContext.builder()
                .customerType(customerType)
                .items(items)
                .coupon(coupon)
                .subtotal(subtotal)
                .promotions(promotions)
                .build();
    }

    public static OrderItemModel item(
            String sku,
            double price,
            int quantity
    ) {
        return OrderItemModel.builder()
                .sku(sku)
                .price(BigDecimal.valueOf(price))
                .quantity(quantity)
                .build();
    }

    public static PromotionConfigDTO promotion(
            PromotionType type,
            double value
    ) {
        return new PromotionConfigDTO(
                type,
                BigDecimal.valueOf(value),
                true
        );
    }

    public static Coupon coupon(
            String code,
            double amount
    ) {
        return Coupon.builder()
                .code(code)
                .discountAmount(BigDecimal.valueOf(amount))
                .build();
    }
}
