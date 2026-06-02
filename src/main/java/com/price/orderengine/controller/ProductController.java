package com.price.orderengine.controller;

import com.price.orderengine.dto.ApiResponse;
import com.price.orderengine.dto.ProductConfigDTO;
import com.price.orderengine.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("")
    @Operation(summary = "Get active product configs")
    public ApiResponse<List<ProductConfigDTO>> getConfigs() {

        return ApiResponse.success(productService.getActiveProducts());
    }
}
