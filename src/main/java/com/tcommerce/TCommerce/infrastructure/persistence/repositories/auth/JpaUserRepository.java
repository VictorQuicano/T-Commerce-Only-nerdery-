package com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
  Optional<UserEntity> findByEmail(String email);
  Boolean existsByEmail(String email);
  List<UserEntity> findByRole(ERole role);
}