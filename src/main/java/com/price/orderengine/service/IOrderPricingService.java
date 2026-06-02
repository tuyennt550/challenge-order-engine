package com.price.orderengine.service;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.CalculateOrderResponse;

public interface IOrderPricingService {
    CalculateOrderResponse calculate(CalculateOrderRequest request);
}
