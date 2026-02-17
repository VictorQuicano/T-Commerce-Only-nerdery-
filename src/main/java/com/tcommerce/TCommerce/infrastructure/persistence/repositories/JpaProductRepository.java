package com.tcommerce.TCommerce.infrastructure.persistence.repositories;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByName(String name);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
