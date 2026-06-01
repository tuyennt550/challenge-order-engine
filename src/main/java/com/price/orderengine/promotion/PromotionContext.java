package com.price.orderengine.promotion;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.enums.CustomerType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class PromotionContext {
    private CustomerType customerType;
    private List<OrderItemModel> items;
    private String couponCode;

    private List<PromotionConfigDTO> promotions;

    private BigDecimal subtotal;
}
