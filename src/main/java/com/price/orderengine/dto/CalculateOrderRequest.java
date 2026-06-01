package com.price.orderengine.dto;

import com.price.orderengine.enums.CustomerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateOrderRequest {
    @NotNull
    private CustomerType customerType;

    @NotEmpty
    private List<@Valid OrderItemRequest> items;

    private String couponCode;
}
