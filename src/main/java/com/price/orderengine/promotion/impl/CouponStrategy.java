package com.price.orderengine.promotion.impl;

import com.price.orderengine.dto.AppliedPromotionDTO;
import com.price.orderengine.entity.Coupon;
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
    public String getType() {
        return PromotionType.COUPON.name();
    }

    @Override
    public boolean isApplicable(PromotionContext context) {
        return context.getCoupon() != null;
    }

    @Override
    public PromotionResult apply(PromotionContext context) {
        Coupon coupon = context.getCoupon();

        if (coupon == null) return PromotionResult.empty();

        BigDecimal discount = coupon.getDiscountAmount();

        return PromotionResult.builder()
                .discount(discount)
                .appliedPromotions(List.of(
                        new AppliedPromotionDTO(
                                getType() + "_" + coupon.getCode(),
                                discount
                        )
                ))
                .build();
    }
}
