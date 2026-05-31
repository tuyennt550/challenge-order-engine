package com.price.orderengine.promotion;

import com.price.orderengine.enums.PromotionType;

public interface PromotionStrategy {
    PromotionType getType();
    PromotionResult apply(PromotionContext context);
}
