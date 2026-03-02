package com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductPriceHistory;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductPriceHistoryRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductPriceHistoryEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.jpa.JpaProductPriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductPriceHistoryRepositoryImpl implements ProductPriceHistoryRepository {

    private final JpaProductPriceHistoryRepository jpaRepository;
    private final JpaProductRepository jpaProductRepository;

    @Override
    public ProductPriceHistory save(ProductPriceHistory history) {
        ProductPriceHistoryEntity entity = ProductPriceHistoryEntity.builder()
                .product(jpaProductRepository.getReferenceById(history.getProductId()))
                .price(history.getPrice())
                .build();
        
        ProductPriceHistoryEntity saved = jpaRepository.save(entity);
        
        return ProductPriceHistory.builder()
                .id(saved.getId())
                .productId(saved.getProduct().getId())
                .price(saved.getPrice())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<ProductPriceHistory> findByProductId(String productId) {
        return jpaRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(entity -> ProductPriceHistory.builder()
                        .id(entity.getId())
                        .productId(entity.getProduct().getId())
                        .price(entity.getPrice())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .toList();
    }
}
