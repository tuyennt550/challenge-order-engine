package com.price.orderengine.promotion;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionEngine {
    private final PromotionChainBuilder builder;

    private PromotionHandler chain;

    @PostConstruct
    void init() {
        this.chain = builder.build();
    }

    public PromotionResult execute(PromotionContext context) {
        return chain.handle(context);
    }
}
