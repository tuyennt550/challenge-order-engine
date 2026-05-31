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
public class PercentageDiscountStrategy implements PromotionStrategy {
    @Override
    public PromotionType getType() {
        return PromotionType.PERCENTAGE_DISCOUNT;
    }

    @Override
    public PromotionResult apply(PromotionContext context) {
        return context.getPromotions().stream()
                .filter(p -> p.getType() == PromotionType.PERCENTAGE_DISCOUNT)
                .findFirst()
                .map(promotion -> {
                    BigDecimal percentage = promotion.getValue();
                    BigDecimal discount = context.getSubtotal().multiply(percentage.divide(BigDecimal.valueOf(100)));

                    return PromotionResult.builder()
                            .discount(discount)
                            .appliedPromotions(List.of(
                                    new AppliedPromotionDTO(
                                            promotion.getType().name(),
                                            discount
                                    )
                            ))
                            .build();
                })
                .orElse(PromotionResult.empty());
    }

}
