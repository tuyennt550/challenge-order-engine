package com.price.orderengine.promotion.impl;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.AppliedPromotionDTO;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.entity.OrderItem;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.promotion.PromotionContext;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.promotion.PromotionStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BuyXGetYStrategy implements PromotionStrategy {
    @Override
    public PromotionType getType() {
        return PromotionType.BUY_2_GET_1_FREE;
    }

    @Override
    public PromotionResult apply(PromotionContext context) {

        return context.getPromotions().stream()
                .filter(p -> p.getType() == PromotionType.BUY_2_GET_1_FREE)
                .findFirst()
                .map(promotion -> {
                    BigDecimal totalDiscount = BigDecimal.ZERO;
                    int buyQuantity = promotion.getValue().intValue();
                    for (OrderItemModel item : context.getItems()) {
                        int quantity = item.getQuantity();
                        int totalFreeItems = quantity / buyQuantity;

                        if (totalFreeItems <= 0) {
                            continue;
                        }

                        BigDecimal itemDiscount = item.getPrice().multiply(BigDecimal.valueOf(totalFreeItems));
                        totalDiscount = totalDiscount.add(itemDiscount);
                    }

                    if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
                        return PromotionResult.builder()
                                .discount(totalDiscount)
                                .appliedPromotions(List.of(
                                        new AppliedPromotionDTO(
                                                promotion.getType().name(),
                                                totalDiscount
                                        )
                                ))
                                .build();
                    }
                    else {
                        return PromotionResult.empty();
                    }
                })
                .orElse(PromotionResult.empty());
    }
}
