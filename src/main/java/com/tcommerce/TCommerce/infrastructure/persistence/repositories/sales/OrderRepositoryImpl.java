package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.OrderMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository, OrderMapper orderMapper) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpaOrderRepository.findById(id)
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return jpaOrderRepository.findByUser_Id(userId).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }
}
