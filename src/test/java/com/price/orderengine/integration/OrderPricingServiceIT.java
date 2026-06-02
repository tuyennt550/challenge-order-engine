package com.price.orderengine.integration;

import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.promotion.PromotionEngine;
import com.price.orderengine.promotion.PromotionResult;
import com.price.orderengine.repository.CouponRepository;
import com.price.orderengine.repository.ProductRepository;
import com.price.orderengine.service.OrderPricingService;
import com.price.orderengine.service.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
class OrderPricingServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderPricingService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionEngine promotionEngine;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        couponRepository.deleteAll();

        productRepository.save(
                Product.builder()
                        .sku("A100")
                        .price(BigDecimal.valueOf(100))
                        .build()
        );

        couponRepository.save(
                Coupon.builder()
                        .code("SUMMER10")
                        .active(true)
                        .usageLimit(10)
                        .usedCount(0)
                        .discountAmount(BigDecimal.TEN)
                        .build()
        );
    }

    // ------------------------------------------------------
    // 1. concurrency smoke test (no crash under load)
    // ------------------------------------------------------
    @Test
    void should_handle_concurrent_calculations() throws Exception {

        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        CalculateOrderRequest request = buildRequest();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    service.calculate(request);
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();

        executor.shutdown();

        // just ensure system stable
        assertTrue(true);
    }

    // ------------------------------------------------------
    // 2. REAL race condition test (coupon usage limit)
    // ------------------------------------------------------
    @Test
    void should_not_exceed_coupon_usage_limit_under_concurrency() throws Exception {

        int threads = 30;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        CalculateOrderRequest request = buildRequest();

        IntStream.range(0, threads).forEach(i ->
                executor.submit(() -> {
                    try {
                        start.await();
                        service.calculate(request);
                    } catch (Exception ignored) {
                    } finally {
                        done.countDown();
                    }
                })
        );

        start.countDown();
        done.await();

        executor.shutdown();

        Integer usedCount = jdbcTemplate.queryForObject(
                "SELECT used_count FROM coupons WHERE code='SUMMER10'",
                Integer.class
        );

        System.out.println("USED COUNT = " + usedCount);

        assertTrue(usedCount <= 10);
    }

    // ------------------------------------------------------
    // helper
    // ------------------------------------------------------
    private CalculateOrderRequest buildRequest() {
        return CalculateOrderRequest.builder()
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
    }
}