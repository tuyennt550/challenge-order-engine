package com.price.orderengine.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NotBlank
    private String sku;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 19, fraction = 2)
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer quantity;
}
