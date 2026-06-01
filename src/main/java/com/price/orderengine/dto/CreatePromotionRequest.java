package com.price.orderengine.dto;

import com.price.orderengine.enums.PromotionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreatePromotionRequest {
    @NotNull
    private PromotionType type;

    @DecimalMin("0.0")
    private BigDecimal value;
    
    private boolean active;
}
