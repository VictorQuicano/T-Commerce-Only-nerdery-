package com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByName(String name);
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);

    @Modifying
    @Query("""
        UPDATE ProductEntity p
        SET p.deletedAt = :deletedAt
        WHERE p.id = :id AND p.deletedAt IS NULL
    """)
    int softDeleteById(@Param("id") String id,
                       @Param("deletedAt") LocalDateTime deletedAt);

}
