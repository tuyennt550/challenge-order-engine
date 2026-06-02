package com.price.orderengine.service;

import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;

import java.util.List;

public interface IPromotionService {
    List<PromotionConfigDTO> getActivePromotions();
    Promotion createPromotion(Promotion promotion);
}
