package com.price.orderengine.dto;

import com.price.orderengine.enums.CustomerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 5)
    private List<@Valid OrderItemRequest> items;

    private String couponCode;
}
