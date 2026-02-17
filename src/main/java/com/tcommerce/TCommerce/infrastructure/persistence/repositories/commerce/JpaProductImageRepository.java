package com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaProductImageRepository extends JpaRepository<ProductImageEntity, String> {
    List<ProductImageEntity> findByProductIdOrderByDisplayOrderAsc(String productId);
    Optional<ProductImageEntity> findByImageUrl(String imageUrl);
    long countByProductId(String productId);
}
