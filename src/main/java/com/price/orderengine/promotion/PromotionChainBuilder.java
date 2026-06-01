package com.price.orderengine.promotion;

import com.price.orderengine.config.PromotionChainProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PromotionChainBuilder {
    private final List<PromotionStrategy> strategies;
    private final PromotionChainProperties properties;

    public PromotionHandler build() {

        Map<String, PromotionStrategy> strategyMap =
                strategies.stream()
                        .collect(Collectors.toMap(
                                PromotionStrategy::getType,
                                s -> s
                        ));

        List<PromotionHandler> ordered = new ArrayList<>();

        properties.getOrder().stream()
                .map(strategyMap::get)
                .filter(Objects::nonNull)
                .forEach(strategy ->
                        ordered.add(new StrategyPromotionHandler(strategy)));

        if (ordered.isEmpty()) {
            throw new IllegalStateException(
                    "No promotion handlers configured"
            );
        }

        for (int i = 0; i < ordered.size() - 1; i++) {
            ordered.get(i).setNext(ordered.get(i + 1));
        }

        return ordered.get(0);
    }
}
