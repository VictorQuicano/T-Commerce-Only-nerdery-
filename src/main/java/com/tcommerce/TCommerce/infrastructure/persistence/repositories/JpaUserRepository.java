package com.tcommerce.TCommerce.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByEmail(String email);

  Boolean existsByEmail(String email);
}