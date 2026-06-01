package com.price.orderengine.dto;

import com.price.orderengine.enums.PromotionType;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromotionConfigDTO {
    private PromotionType type;
    private BigDecimal value;
    private Boolean active;
}
