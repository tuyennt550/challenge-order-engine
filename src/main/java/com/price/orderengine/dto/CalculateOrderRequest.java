package com.price.orderengine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateOrderRequest {
    private String customerType;

    @NotEmpty
    private List<@Valid OrderItemRequest> items;

    private String couponCode;
}
