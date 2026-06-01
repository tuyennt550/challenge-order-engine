package com.price.orderengine.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OrderItemModel {
    private String sku;
    private BigDecimal price;
    private Integer quantity;
}
