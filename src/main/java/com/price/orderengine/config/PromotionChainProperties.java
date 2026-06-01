package com.price.orderengine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "promotion.chain")
public class PromotionChainProperties {
    private List<String> order = new ArrayList<>();
}
