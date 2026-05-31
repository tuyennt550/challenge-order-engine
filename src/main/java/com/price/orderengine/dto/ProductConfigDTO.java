package com.price.orderengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductConfigDTO {
    private String sku;
    private String name;
    private BigDecimal price;
}
