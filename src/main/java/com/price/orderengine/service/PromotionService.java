package com.price.orderengine.service;

import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.mapper.PromotionMapper;
import com.price.orderengine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Cacheable(cacheNames = "promotion-active")
    public List<PromotionConfigDTO> getActivePromotions() {
        List<Promotion> promotions = promotionRepository.findByActiveTrue();
        return promotionMapper.toDto(promotions);
    }

    @CacheEvict(cacheNames = {
            "promotion-active"
    }, allEntries = true)
    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

}
