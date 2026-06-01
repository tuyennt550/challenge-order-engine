package com.price.orderengine.promotion;

public abstract class AbstractPromotionHandler implements PromotionHandler {
    protected PromotionHandler next;

    @Override
    public void setNext(PromotionHandler next) {
        this.next = next;
    }

    protected PromotionResult proceed(
            PromotionContext context,
            PromotionResult current) {

        if (next == null) {
            return current;
        }

        return current.merge(next.handle(context));
    }
}
