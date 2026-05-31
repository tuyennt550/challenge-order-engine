package com.price.orderengine.promotion;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.PromotionConfigDTO;

import java.math.BigDecimal;
import java.util.List;

public class PromotionTestHelper {
    static PromotionContext createContext(
            BigDecimal subtotal,
            List<PromotionConfigDTO> promotions,
            CalculateOrderRequest request
    ) {
        return PromotionContext.builder()
                .request(request)
                .promotions(promotions)
                .subtotal(subtotal)
                .build();
    }
}
