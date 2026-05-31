package com.price.orderengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CalculateOrderResponse {
    private BigDecimal subtotal;
    private List<AppliedPromotionDTO> discounts;
    private BigDecimal totalDiscount;
    private BigDecimal finalPrice;
}
