package com.price.orderengine.service;

import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.mapper.PromotionMapper;
import com.price.orderengine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Cacheable(cacheNames = "promotion-active")
    public List<PromotionConfigDTO> getActivePromotions() {
        List<Promotion> promotions = promotionRepository.findByActiveTrue();
        return promotionMapper.toDto(promotions);
    }

    @CacheEvict(cacheNames = {
            "promotion-active"
    }, allEntries = true)
    public Promotion updatePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @CacheEvict(cacheNames = {
            "promotion-active"
    }, allEntries = true)
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @CacheEvict(cacheNames = {
            "promotion-active"
    }, allEntries = true)
    public void deletePromotion(UUID id) {
        promotionRepository.deleteById(id);
    }

}
