package com.price.orderengine.promotion;

import com.price.orderengine.dto.AppliedPromotionDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PromotionResult {
    private final BigDecimal discount;
    private final List<AppliedPromotionDTO> appliedPromotions;

    public static PromotionResult empty() {
        return PromotionResult.builder()
                .discount(BigDecimal.ZERO)
                .appliedPromotions(List.of())
                .build();
    }

    public PromotionResult merge(PromotionResult other) {

        List<AppliedPromotionDTO> mergedPromotions = new ArrayList<>(appliedPromotions);
        mergedPromotions.addAll(other.appliedPromotions);

        return PromotionResult.builder()
                .discount(discount.add(other.discount))
                .appliedPromotions(mergedPromotions)
                .build();
    }
}
