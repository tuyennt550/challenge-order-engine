package com.price.orderengine.service;

import com.price.orderengine.dto.PromotionConfigDTO;

import java.util.List;

public interface IPromotionService {
    List<PromotionConfigDTO> getActivePromotions();
}
