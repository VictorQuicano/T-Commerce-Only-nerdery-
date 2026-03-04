package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {
    List<OrderEntity> findByUser_Id(String userId);
    List<OrderEntity> findByStatus(OrderStatus status);
    List<OrderEntity> findByStatusAndUpdatedAtBefore(
        OrderStatus status,
        LocalDateTime dateTime
);
}
