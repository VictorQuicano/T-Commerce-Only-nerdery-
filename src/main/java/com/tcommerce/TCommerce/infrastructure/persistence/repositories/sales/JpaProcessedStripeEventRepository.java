package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.ProcessedStripeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProcessedStripeEventRepository extends JpaRepository<ProcessedStripeEventEntity, String> {
    boolean existsByEventId(String eventId);
}
