package com.price.orderengine.service;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.CalculateOrderResponse;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.errors.DBNotFoundException;
import com.price.orderengine.errors.UserFriendlyException;
import com.price.orderengine.promotion.PromotionEngine;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.repository.CouponRepository;
import com.price.orderengine.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderPricingServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private PromotionService promotionService;

    @Mock
    private PromotionEngine promotionEngine;

    @InjectMocks
    private OrderPricingService service;

    @Test
    void should_throw_when_coupon_not_found() {
        when(productRepository.findBySkuIn(any()))
                .thenReturn(List.of(
                        Product.builder()
                                .sku("A100")
                                .price(BigDecimal.valueOf(100))
                                .build()
                ));

        when(couponRepository.findByCodeAndActiveTrue("SUMMER10"))
                .thenReturn(Optional.empty());

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.REGULAR)
                        .couponCode("SUMMER10")
                        .items(List.of(
                                OrderItemRequest.builder()
                                        .sku("A100")
                                        .price(BigDecimal.valueOf(100))
                                        .quantity(1)
                                        .build()
                        ))
                        .build();

        assertThrows(
                DBNotFoundException.class,
                () -> service.calculate(request)
        );
        verify(couponRepository).findByCodeAndActiveTrue("SUMMER10");
    }

    @Test
    void should_throw_when_coupon_expired() {

        Coupon coupon = Coupon.builder()
                .code("SUMMER10")
                .active(true)
                .discountAmount(BigDecimal.TEN)
                .expiryDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        when(productRepository.findBySkuIn(any()))
                .thenReturn(List.of(
                        Product.builder()
                                .sku("A100")
                                .price(BigDecimal.valueOf(100))
                                .build()
                ));

        when(couponRepository.findByCodeAndActiveTrue("SUMMER10"))
                .thenReturn(Optional.of(coupon));

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.REGULAR)
                        .couponCode("SUMMER10")
                        .items(List.of(
                                OrderItemRequest.builder()
                                        .sku("A100")
                                        .price(BigDecimal.valueOf(100))
                                        .quantity(1)
                                        .build()
                        ))
                        .build();

        assertThrows(
                UserFriendlyException.class,
                () -> service.calculate(request)
        );
    }

    @Test
    void should_calculate_order_successfully() {

        when(productRepository.findBySkuIn(any()))
                .thenReturn(List.of(
                        Product.builder()
                                .sku("A100")
                                .price(BigDecimal.valueOf(100))
                                .build()
                ));

        when(promotionService.getActivePromotions())
                .thenReturn(List.of());

        when(promotionEngine.execute(any()))
                .thenReturn(
                        PromotionResult.builder()
                                .discount(BigDecimal.TEN)
                                .appliedPromotions(List.of())
                                .build()
                );

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.REGULAR)
                        .items(List.of(
                                OrderItemRequest.builder()
                                        .sku("A100")
                                        .price(BigDecimal.valueOf(100))
                                        .quantity(2)
                                        .build()
                        ))
                        .build();

        CalculateOrderResponse result =
                service.calculate(request);

        assertEquals(
                0,
                BigDecimal.valueOf(200)
                        .compareTo(result.getSubtotal())
        );

        assertEquals(
                0,
                BigDecimal.TEN
                        .compareTo(result.getTotalDiscount())
        );

        assertEquals(
                0,
                BigDecimal.valueOf(190)
                        .compareTo(result.getFinalPrice())
        );
    }
}
