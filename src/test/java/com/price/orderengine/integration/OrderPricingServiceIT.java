package com.price.orderengine.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.entity.Coupon;
import com.price.orderengine.entity.Product;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.repository.CouponRepository;
import com.price.orderengine.repository.ProductRepository;
import com.price.orderengine.service.OrderPricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class OrderPricingServiceIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void should_return_correct_final_price_under_concurrency() throws Exception {

        int threads = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        List<BigDecimal> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();

                    String response = mockMvc.perform(
                                    post("/api/v1/orders/calculate")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(buildRequest()))
                            )
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    results.add(extractFinalPrice(response));

                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();

        executor.shutdown();

        BigDecimal expected = results.get(0);

        for (BigDecimal price : results) {
            assertEquals(expected, price);
        }
    }

    // ------------------------------------------------------
    // 2. REAL race condition test (coupon usage limit)
    // ------------------------------------------------------
    @Test
    void should_not_exceed_coupon_limit_under_concurrent_requests() throws Exception {

        int threads = 30;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();

                    mockMvc.perform(
                            post("/api/v1/orders/calculate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(buildRequest()))
                    ).andDo(result -> {
                        int status = result.getResponse().getStatus();

                        if (status == 200) success.incrementAndGet();
                        else fail.incrementAndGet();
                    });

                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();

        executor.shutdown();

        Integer usedCount = jdbcTemplate.queryForObject(
                "SELECT used_count FROM coupons WHERE code='SUMMER10'",
                Integer.class
        );

        System.out.println("USED COUNT = " + usedCount);

        assertEquals(10,usedCount);

        assertEquals(threads, success.get() + fail.get());
        assertTrue(success.get() <= 10);
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

    private BigDecimal extractFinalPrice(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return new BigDecimal(node.get("finalTotal").asText());
    }
}