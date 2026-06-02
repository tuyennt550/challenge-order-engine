package com.price.orderengine.service;

import com.price.orderengine.dto.ProductConfigDTO;
import com.price.orderengine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    @Override
    @Cacheable(cacheNames = "product-configs")
    public List<ProductConfigDTO> getActiveProducts() {

        return productRepository.findActiveProductConfigs();
    }
}
