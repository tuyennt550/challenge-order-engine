package com.price.orderengine.promotion;

import com.price.orderengine.dto.AppliedPromotionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionEngine {
    private final List<PromotionStrategy> strategies;

    public PromotionResult execute(PromotionContext context) {
        List<PromotionStrategy> sorted = strategies.stream()
                .sorted(Comparator.comparingInt(s -> s.getType().ordinal()))
                .toList();

        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<AppliedPromotionDTO> allApplied = new ArrayList<>();

        for (PromotionStrategy strategy : sorted) {
            PromotionResult result = strategy.apply(context);

            totalDiscount = totalDiscount.add(result.getDiscount());
            allApplied.addAll(result.getAppliedPromotions());
        }
        return PromotionResult.builder()
                .discount(totalDiscount)
                .appliedPromotions(allApplied)
                .build();
    }
}
