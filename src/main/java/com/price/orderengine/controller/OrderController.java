package com.price.orderengine.controller;

import com.price.orderengine.dto.ApiResponse;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.CalculateOrderResponse;
import com.price.orderengine.service.OrderPricingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderPricingService orderPricingService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate order price")
    public ApiResponse<CalculateOrderResponse> calculate(@Valid @RequestBody CalculateOrderRequest request) {
        return ApiResponse.success(orderPricingService.calculate(request));
    }
}
