package com.tcommerce.TCommerce.domain.repositories.interfaces.auth;

import com.tcommerce.TCommerce.domain.entities.auth.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository   {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    User save(User user);
    List<User> findAll();
    Optional<User> findById(String id);
    void deleteById(String id);

}
