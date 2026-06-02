package com.price.orderengine.promotion;

public class StrategyPromotionHandler extends AbstractPromotionHandler {
    private final PromotionStrategy strategy;

    public StrategyPromotionHandler(PromotionStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String getType() {
        return strategy.getType();
    }

    @Override
    public PromotionResult handle(PromotionContext context) {
        if(strategy.isApplicable(context)) {
            PromotionResult current = strategy.apply(context);

            return proceed(context, current);
        }
        return proceed(context, PromotionResult.empty());

    }
}
