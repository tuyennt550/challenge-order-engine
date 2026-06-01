package com.price.orderengine.promotion;

public interface PromotionStrategy {
    String getType();
    PromotionResult apply(PromotionContext context);
}
