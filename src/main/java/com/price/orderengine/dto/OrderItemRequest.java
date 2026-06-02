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

    /**
     * price is provided for client-side display/debugging only.
     * It is NOT used in calculation.
     * Server always overrides with product master price.
     */
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Digits(integer = 19, fraction = 2)
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer quantity;
}
