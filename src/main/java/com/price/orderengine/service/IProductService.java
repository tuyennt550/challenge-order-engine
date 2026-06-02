package com.price.orderengine.service;

import com.price.orderengine.dto.ProductConfigDTO;

import java.util.List;

public interface IProductService {
    List<ProductConfigDTO> getActiveProducts();
}
