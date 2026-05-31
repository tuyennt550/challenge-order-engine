package com.price.orderengine.repository;

import com.price.orderengine.dto.ProductConfigDTO;
import com.price.orderengine.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);

    List<Product> findBySkuIn(List<String> skus);

    @Query("""
        select new com.price.orderengine.dto.ProductConfigDTO(
            p.sku,
            p.name,
            p.price
        )
        from Product p
        order by p.name
    """)
    List<ProductConfigDTO> findActiveProductConfigs();
}
