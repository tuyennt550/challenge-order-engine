package com.price.orderengine.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.entity.Product;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.repository.ProductRepository;
import com.price.orderengine.repository.PromotionRepository;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class CalculateOrderIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @BeforeEach
    void setup() {

        promotionRepository.deleteAll();
        productRepository.deleteAll();

        productRepository.save(
                Product.builder()
                        .sku("A100")
                        .name("Product A")
                        .price(new BigDecimal("100"))
                        .build()
        );

        promotionRepository.save(
                Promotion.builder()
                        .type(PromotionType.PERCENTAGE_DISCOUNT)
                        .value(new BigDecimal("10"))
                        .active(true)
                        .createdAt(Instant.now())
                        .build()
        );

        promotionRepository.save(
                Promotion.builder()
                        .type(PromotionType.VIP_DISCOUNT)
                        .value(new BigDecimal("20"))
                        .active(true)
                        .createdAt(Instant.now())
                        .build()
        );
    }

    @Test
    void should_calculate_final_price_using_real_promotions()
            throws Exception {

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.VIP.name())
                        .items(
                                List.of(
                                        OrderItemRequest.builder()
                                                .sku("A100")
                                                .quantity(2)
                                                .build()
                                )
                        )
                        .build();

        mockMvc.perform(
                        post("/api/v1/orders/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.subtotal")
                        .value(200))

                .andExpect(jsonPath("$.data.totalDiscount")
                        .value(60))

                .andExpect(jsonPath("$.data.finalPrice")
                        .value(140));
    }
}