package com.price.orderengine.promotion.impl;

import com.price.orderengine.dto.AppliedPromotionDTO;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.PromotionContext;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.promotion.PromotionStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CouponStrategy implements PromotionStrategy {
    @Override
    public PromotionType getType() {
        return PromotionType.COUPON;
    }

    @Override
    public PromotionResult apply(PromotionContext context) {
        return context.getPromotions().stream()
                .filter(p -> p.getType() == PromotionType.COUPON)
                .findFirst()
                .map(promotion -> {
                    String couponCode = context.getRequest().getCouponCode();
                    BigDecimal discount = promotion.getValue();

                    return PromotionResult.builder()
                            .discount(discount)
                            .appliedPromotions(List.of(
                                    new AppliedPromotionDTO(
                                            promotion.getType().name() + "_" + couponCode,
                                            discount
                                    )
                            ))
                            .build();
                })
                .orElse(PromotionResult.empty());
    }
}
