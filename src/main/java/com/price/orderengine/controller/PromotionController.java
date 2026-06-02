package com.price.orderengine.controller;

import com.price.orderengine.dto.ApiResponse;
import com.price.orderengine.dto.CreatePromotionRequest;
import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.mapper.PromotionMapper;
import com.price.orderengine.service.IPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions")
public class PromotionController {
    private final IPromotionService promotionService;
    private final PromotionMapper promotionMapper;

    @GetMapping("")
    @Operation(summary = "Get promotion configs")
    public ApiResponse<List<PromotionConfigDTO>> getConfigs() {

        return ApiResponse.success(promotionService.getActivePromotions());
    }

    @PostMapping("")
    @Operation(summary = "Create a promotion")
    public ResponseEntity<ApiResponse<PromotionConfigDTO>> createPromotion(@Valid @RequestBody CreatePromotionRequest createPromotionRequest) {
        Promotion promotion = Promotion.builder()
                .type(createPromotionRequest.getType())
                .value(createPromotionRequest.getValue())
                .active(createPromotionRequest.isActive())
                .build();

        Promotion saved = promotionService.createPromotion(promotion);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promotionMapper.toDto(saved)));
    }
}
