package com.price.orderengine.promotion;

public interface PromotionHandler {
    String getType();
    void setNext(PromotionHandler next);
    PromotionResult handle(PromotionContext context);
}
