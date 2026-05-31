package com.price.orderengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class AppliedPromotionDTO {
    private String type;
    private BigDecimal amount;
}
