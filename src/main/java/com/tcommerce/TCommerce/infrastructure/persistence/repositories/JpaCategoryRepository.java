package com.tcommerce.TCommerce.infrastructure.persistence.repositories;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
