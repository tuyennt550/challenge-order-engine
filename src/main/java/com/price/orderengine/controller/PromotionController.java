package com.price.orderengine.controller;

import com.price.orderengine.dto.ApiResponse;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("")
    @Operation(summary = "Get promotion configs")
    public ApiResponse<List<PromotionConfigDTO>> getConfigs() {

        return ApiResponse.success(promotionService.getActivePromotions());
    }
}
