package com.price.orderengine.service;

import com.price.orderengine.domain.model.OrderItemModel;
import com.price.orderengine.dto.*;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.errors.DBNotFoundException;
import com.price.orderengine.errors.ErrorCode;
import com.price.orderengine.promotion.PromotionContext;
import com.price.orderengine.promotion.PromotionEngine;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderPricingService implements IOrderPricingService {
    private final ProductRepository productRepository;
    private final PromotionEngine promotionEngine;
    private final IPromotionService promotionService;
    private final ICouponService couponService;

    @Override
    public CalculateOrderResponse calculate(CalculateOrderRequest request) {
        Map<String, Product> productMap = validateProducts(request);
        List<OrderItemModel> items = request.getItems().stream()
                .map(item -> toDomainItem(item, productMap))
                .toList();

        BigDecimal subtotal = calculateSubtotal(request, productMap);

        /*
         * Load active promotions
         */
        List<PromotionConfigDTO> activePromotions = promotionService.getActivePromotions();

        /*
         * Apply coupon
         */
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            couponService.validateCoupon(request.getCouponCode());

            coupon = couponService.reserveCoupon(request.getCouponCode());
        }

        /*
         * Apply normal promotions
         */
        PromotionContext context = PromotionContext.builder()
                .customerType(request.getCustomerType())
                .items(items)
                .coupon(coupon)
                .promotions(activePromotions)
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
