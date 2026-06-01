package com.price.orderengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.price.orderengine.dto.CreatePromotionRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.enums.PromotionType;
import com.price.orderengine.mapper.PromotionMapper;
import com.price.orderengine.service.PromotionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotionController.class)
public class PromotionControllerTest {
    @MockBean
    private PromotionService promotionService;

    @MockBean
    private PromotionMapper promotionMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_get_active_promotions_successfully() throws Exception {

        List<PromotionConfigDTO> response = List.of(
                new PromotionConfigDTO(
                        PromotionType.PERCENTAGE_DISCOUNT,
                        BigDecimal.valueOf(10),
                        true)
        );

        when(promotionService.getActivePromotions())
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("PERCENTAGE_DISCOUNT"))
                .andExpect(jsonPath("$.data[0].value").value(10))
                .andExpect(jsonPath("$.data[0].active").value(true));

        verify(promotionService).getActivePromotions();
    }

    @Test
    void should_create_promotion_successfully() throws Exception {

        CreatePromotionRequest request = CreatePromotionRequest.builder()
                .type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(BigDecimal.valueOf(10))
                .active(true)
                .build();

        Promotion saved = Promotion.builder()
                .type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(BigDecimal.valueOf(10))
                .active(true)
                .build();

        PromotionConfigDTO dto = PromotionConfigDTO.builder()
                .type(PromotionType.PERCENTAGE_DISCOUNT)
                .value(BigDecimal.valueOf(10))
                .active(true)
                .build();

        when(promotionService.createPromotion(any(Promotion.class)))
                .thenReturn(saved);

        when(promotionMapper.toDto(saved))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("PERCENTAGE_DISCOUNT"))
                .andExpect(jsonPath("$.data.value").value(10))
                .andExpect(jsonPath("$.data.active").value(true));

        verify(promotionService).createPromotion(any(Promotion.class));
        verify(promotionMapper).toDto(saved);
    }

    @Test
    void should_fail_when_create_promotion_invalid() throws Exception {

        CreatePromotionRequest request = CreatePromotionRequest.builder()
                .type(null)
                .value(null)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(promotionService, never()).createPromotion(any());
    }
}
