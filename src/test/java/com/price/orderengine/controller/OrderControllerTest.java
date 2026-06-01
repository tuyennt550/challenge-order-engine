
package com.price.orderengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.dto.CalculateOrderResponse;
import com.price.orderengine.dto.OrderItemRequest;
import com.price.orderengine.enums.CustomerType;
import com.price.orderengine.service.OrderPricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderPricingService orderPricingService;

    @Test
    void should_calculate_order_successfully() throws Exception {

        CalculateOrderResponse response =
                CalculateOrderResponse.builder()
                        .subtotal(BigDecimal.valueOf(250.00))
                        .totalDiscount(BigDecimal.valueOf(147.50))
                        .finalPrice(BigDecimal.valueOf(102.50))
                        .build();

        when(orderPricingService.calculate(any()))
                .thenReturn(response);

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.VIP)
                        .items(List.of(
                                OrderItemRequest.builder()
                                        .sku("A100")
                                        .price(BigDecimal.valueOf(100))
                                        .quantity(2)
                                        .build(),
                                OrderItemRequest.builder()
                                        .sku("B200")
                                        .price(BigDecimal.valueOf(50))
                                        .quantity(1)
                                        .build()
                        ))
                        .couponCode("SUMMER10")
                        .build();

        mockMvc.perform(
                        post("/api/v1/orders/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subtotal").value(250.00))
                .andExpect(jsonPath("$.data.totalDiscount").value(147.50))
                .andExpect(jsonPath("$.data.finalPrice").value(102.50))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void should_fail_when_items_empty() throws Exception {

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.VIP)
                        .items(List.of())
                        .couponCode("SUMMER10")
                        .build();

        mockMvc.perform(
                        post("/api/v1/orders/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void should_fail_when_items_null() throws Exception {

        CalculateOrderRequest request =
                CalculateOrderRequest.builder()
                        .customerType(CustomerType.VIP)
                        .items(null)
                        .couponCode("SUMMER10")
                        .build();

        mockMvc.perform(
                        post("/api/v1/orders/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
