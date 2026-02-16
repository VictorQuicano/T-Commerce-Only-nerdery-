package com.tcommerce.TCommerce.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;

import java.util.List;
import java.util.Optional;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByEmail(String email);
  Boolean existsByEmail(String email);
}