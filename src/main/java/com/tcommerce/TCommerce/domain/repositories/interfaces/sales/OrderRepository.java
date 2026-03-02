package com.tcommerce.TCommerce.domain.repositories.interfaces.sales;

import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    List<Order> findByUserId(String userId);
    Window<Order> findAll(ScrollPosition position, int limit, OrderFilter filter, Sort sort);
    Order save(Order order);
    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime dateTime);
}
