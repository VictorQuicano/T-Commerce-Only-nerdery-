package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderStatusHistoryEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.OrderMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

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
    public Window<Order> findAll(ScrollPosition position, int limit, OrderFilter filter, Sort sort) {
        Specification<OrderEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (filter.status() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.status()));
                }
                if (filter.userId() != null && !filter.userId().isEmpty()) {
                    predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jpaOrderRepository.findBy(spec, q -> q
                .sortBy(sort)
                .limit(limit)
                .scroll(position))
                .map(orderMapper::toDomain);
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // For existing orders: fetch the managed entity and update fields in-place.
        // This avoids ObjectOptimisticLockingFailureException caused by merging
        // a manually-constructed detached entity with cascaded children.
        if (order.getId() != null) {
            Optional<OrderEntity> existing = jpaOrderRepository.findById(order.getId());
            if (existing.isPresent()) {
                OrderEntity entity = existing.get();
                entity.setStatus(order.getStatus());
                entity.setPaymentIntentId(order.getPaymentIntentId());
                entity.setUpdatedAt(order.getUpdatedAt());

                // Append any new status history entries (identified by null id)
                List<OrderStatusHistoryEntity> newHistoryEntries = order.getStatusHistory().stream()
                        .filter(h -> h.getId() == null)
                        .map(h -> orderMapper.toEntity(h, entity))
                        .collect(Collectors.toList());
                entity.getStatusHistory().addAll(newHistoryEntries);

                // JPA dirty-checks automatically — no explicit save() needed,
                // but we call it here to ensure flush within the same transaction.
                OrderEntity savedEntity = jpaOrderRepository.save(entity);
                return orderMapper.toDomain(savedEntity);
            }
        }

        // New order: use the full mapper path
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }
}

