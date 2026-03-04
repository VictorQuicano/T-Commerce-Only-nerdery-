package com.tcommerce.TCommerce.domain.repositories.interfaces.sales;

import com.tcommerce.TCommerce.domain.entities.sales.ProcessedStripeEvent;

public interface ProcessedStripeEventRepository {
    boolean existsByEventId(String eventId);
    void save(ProcessedStripeEvent event);
}
