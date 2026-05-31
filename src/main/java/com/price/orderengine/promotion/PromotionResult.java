package com.price.orderengine.promotion;

import com.price.orderengine.dto.AppliedPromotionDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
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
}
