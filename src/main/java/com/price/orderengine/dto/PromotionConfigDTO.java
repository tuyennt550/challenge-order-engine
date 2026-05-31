package com.price.orderengine.dto;

import com.price.orderengine.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionConfigDTO {
    private PromotionType type;
    private BigDecimal value;
    private Boolean active;
}
