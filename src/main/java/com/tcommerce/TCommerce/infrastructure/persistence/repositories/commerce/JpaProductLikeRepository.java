package com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProductLikeRepository extends JpaRepository<ProductLikeEntity, String> {
    Optional<ProductLikeEntity> findByProductIdAndUserId(String productId, String userId);
    boolean existsByProductIdAndUserId(String productId, String userId);
    long countByProductId(String productId);
    void deleteByProductIdAndUserId(String productId, String userId);
}
