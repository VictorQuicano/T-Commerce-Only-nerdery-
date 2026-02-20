package com.tcommerce.TCommerce.domain.repositories.interfaces.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Cart;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);
    Cart save(Cart cart);
    void deleteByUserId(String userId);
}
