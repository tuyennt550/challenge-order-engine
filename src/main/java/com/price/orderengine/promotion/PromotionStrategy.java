package com.price.orderengine.promotion;

public interface PromotionStrategy {
    String getType();
    boolean isApplicable(PromotionContext context);
    PromotionResult apply(PromotionContext context);
}
