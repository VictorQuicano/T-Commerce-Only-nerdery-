package com.tcommerce.TCommerce.domain.repositories.interfaces.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    List<Order> findByUserId(String userId);
    Order save(Order order);
}
