package com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Refund;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.RefundEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales.JpaOrderRepository;
import org.springframework.stereotype.Component;

@Component
public class RefundMapper {

    private final JpaOrderRepository orderRepository;

    public RefundMapper(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Refund toDomain(RefundEntity entity) {
        if (entity == null) return null;

        return Refund.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .stripeRefundId(entity.getStripeRefundId())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RefundEntity toEntity(Refund domain) {
        if (domain == null) return null;

        OrderEntity orderEntity = orderRepository.findById(domain.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + domain.getOrderId()));

        return RefundEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .amount(domain.getAmount())
                .status(domain.getStatus())
                .stripeRefundId(domain.getStripeRefundId())
                .reason(domain.getReason())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
