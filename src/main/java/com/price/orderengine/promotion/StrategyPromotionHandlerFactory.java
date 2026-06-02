package com.price.orderengine.promotion;

import org.springframework.stereotype.Component;

@Component
public class StrategyPromotionHandlerFactory {
    public PromotionHandler create(PromotionStrategy strategy) {
        return new StrategyPromotionHandler(strategy);
    }
}
