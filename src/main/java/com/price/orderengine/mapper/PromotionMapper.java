package com.price.orderengine.mapper;

import com.price.orderengine.dto.PromotionConfigDTO;
import com.price.orderengine.entity.Promotion;
import com.price.orderengine.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface PromotionMapper extends EntityMapper<PromotionConfigDTO, Promotion> {
}
