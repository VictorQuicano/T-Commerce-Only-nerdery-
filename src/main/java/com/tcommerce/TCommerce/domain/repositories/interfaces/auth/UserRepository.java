package com.tcommerce.TCommerce.domain.repositories.interfaces.auth;

import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;

import java.util.Optional;

public interface UserRepository extends CRUDRepository<User> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
