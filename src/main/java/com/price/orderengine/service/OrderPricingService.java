package com.price.orderengine.service;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.*;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.errors.DBNotFoundException;
import com.price.orderengine.errors.ErrorCode;
import com.price.orderengine.errors.UserFriendlyException;
import com.price.orderengine.promotion.PromotionContext;
import com.price.orderengine.promotion.PromotionEngine;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.repository.CouponRepository;
import com.price.orderengine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPricingService {
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final PromotionEngine promotionEngine;
    private final PromotionService promotionService;

    public CalculateOrderResponse calculate(CalculateOrderRequest request) {
        Map<String, Product> productMap = validateProducts(request);
        List<OrderItemModel> items = request.getItems().stream()
                .map(item -> toDomainItem(item, productMap))
                .toList();

        BigDecimal subtotal = calculateSubtotal(request, productMap);

        /*
         * Load active promotions
         */
        List<PromotionConfigDTO> promotions = promotionService.getActivePromotions();

        /*
         * Apply coupon
         */
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(request.getCouponCode()).orElse(null);

            if(coupon == null) {
                throw new DBNotFoundException(ErrorCode.COUPON_NOT_FOUND, "Coupon not found: " + request.getCouponCode());
            }
            if(!coupon.isValid()) {
                throw new UserFriendlyException(ErrorCode.INVALID_COUPON, "Invalid coupon: " + coupon.getCode());
            }

            promotions.add(new PromotionConfigDTO(
                    PromotionType.COUPON,
                    coupon.getDiscountAmount(),
                    true
            ));
        }

        /*
         * Apply normal promotions
         */
        PromotionContext context = PromotionContext.builder()
                .customerType(request.getCustomerType())
                .items(items)
                .couponCode(request.getCouponCode())
                .promotions(promotions)
                .subtotal(subtotal)
                .build();
        PromotionResult discounts = promotionEngine.execute(context);

        /*
         * Prevent negative final price
         */
        BigDecimal totalDiscount = discounts.getDiscount();
        if (totalDiscount.compareTo(subtotal) > 0) {
            totalDiscount = subtotal;
        }

        BigDecimal finalPrice = subtotal.subtract(totalDiscount);

        return CalculateOrderResponse
            .builder()
            .subtotal(subtotal)
            .discounts(discounts.getAppliedPromotions())
            .totalDiscount(totalDiscount)
            .finalPrice(finalPrice)
            .build();
    }

    private Map<String, Product> validateProducts(CalculateOrderRequest request) {
        List<String> skus = request.getItems()
            .stream()
            .map(OrderItemRequest::getSku)
            .toList();

        List<Product> products = productRepository.findBySkuIn(skus);

        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getSku, p -> p));

        for (OrderItemRequest item : request.getItems()) {
            Product product = productMap.get(item.getSku());

            /*
             * Product not found
             */
            if (product == null) {
                throw new DBNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + item.getSku());
            }
        }

        return productMap;
    }

    private BigDecimal calculateSubtotal(CalculateOrderRequest request, Map<String, Product> productMap) {
        return request.getItems()
            .stream()
            .map(item -> {
                Product product = productMap.get(item.getSku());
                return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemModel toDomainItem(OrderItemRequest request, Map<String, Product> productMap) {
        Product product = productMap.get(request.getSku());

        return OrderItemModel.builder()
                .sku(request.getSku())
                .price(product.getPrice())
                .quantity(request.getQuantity())
                .build();
    }
}
