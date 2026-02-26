package com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderItem;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatusHistory;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderItemEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderStatusHistoryEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaUserRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final JpaUserRepository userRepository;
    private final JpaProductRepository productRepository;

    public OrderMapper(JpaUserRepository userRepository, JpaProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .status(entity.getStatus())
                .items(entity.getItems().stream().map(this::toDomain).collect(Collectors.toCollection(ArrayList::new)))
                .statusHistory(entity.getStatusHistory().stream().map(this::toDomain).collect(Collectors.toCollection(ArrayList::new)))
                .paymentIntentId(entity.getPaymentIntentId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;

        return OrderItem.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProductName())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OrderStatusHistory toDomain(OrderStatusHistoryEntity entity) {
        if (entity == null) return null;

        return OrderStatusHistory.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .fromStatus(entity.getFromStatus())
                .toStatus(entity.getToStatus())
                .changedAt(entity.getChangedAt())
                .changedBy(entity.getChangedBy() != null ? entity.getChangedBy().getId() : null)
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OrderEntity toEntity(Order domain) {
        if (domain == null) return null;

        UserEntity user = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + domain.getUserId()));

        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .user(user)
                .status(domain.getStatus())
                .paymentIntentId(domain.getPaymentIntentId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toEntity(item, entity))
                .collect(Collectors.toCollection(ArrayList::new));
        entity.setItems(itemEntities);

        List<OrderStatusHistoryEntity> historyEntities = domain.getStatusHistory().stream()
                .map(history -> toEntity(history, entity))
                .collect(Collectors.toCollection(ArrayList::new));
        entity.setStatusHistory(historyEntities);

        return entity;
    }

    public OrderItemEntity toEntity(OrderItem domain, OrderEntity orderEntity) {
        if (domain == null) return null;

        ProductEntity product = productRepository.findById(domain.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + domain.getProductId()));

        return OrderItemEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .product(product)
                .productName(domain.getProductName())
                .quantity(domain.getQuantity())
                .price(domain.getPrice())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public OrderStatusHistoryEntity toEntity(OrderStatusHistory domain, OrderEntity orderEntity) {
        if (domain == null) return null;

        UserEntity changer = null;
        if (domain.getChangedBy() != null) {
            changer = userRepository.findById(domain.getChangedBy())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + domain.getChangedBy()));
        }

        return OrderStatusHistoryEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .fromStatus(domain.getFromStatus())
                .toStatus(domain.getToStatus())
                .changedAt(domain.getChangedAt())
                .changedBy(changer)
                .reason(domain.getReason())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
