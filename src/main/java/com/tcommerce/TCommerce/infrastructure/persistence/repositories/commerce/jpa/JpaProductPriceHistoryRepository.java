package com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.jpa;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductPriceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductPriceHistoryRepository extends JpaRepository<ProductPriceHistoryEntity, String> {
    List<ProductPriceHistoryEntity> findByProductIdOrderByCreatedAtDesc(String productId);
}
