package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaRefundRepository extends JpaRepository<RefundEntity, String> {
    List<RefundEntity> findByOrder_Id(String orderId);
}
