package com.price.orderengine.promotion;

import com.price.orderengine.dto.AppliedPromotionDTO;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class PromotionContext {
    private CalculateOrderRequest request;
    private List<PromotionConfigDTO> promotions;

    private BigDecimal subtotal;
}
