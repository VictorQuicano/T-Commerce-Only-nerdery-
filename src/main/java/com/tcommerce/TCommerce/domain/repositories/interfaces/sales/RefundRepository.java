package com.tcommerce.TCommerce.domain.repositories.interfaces.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Refund;
import java.util.List;
import java.util.Optional;

public interface RefundRepository {
    Refund save(Refund refund);
    Optional<Refund> findById(String id);
    List<Refund> findByOrderId(String orderId);
}
