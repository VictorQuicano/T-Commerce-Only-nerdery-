package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCartRepository extends JpaRepository<CartEntity, String> {
    Optional<CartEntity> findByUser_Id(String userId);
    void deleteByUser_Id(String userId);
}
