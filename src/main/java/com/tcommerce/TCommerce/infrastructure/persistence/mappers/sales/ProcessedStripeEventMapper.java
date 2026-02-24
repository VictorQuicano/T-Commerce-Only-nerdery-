package com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales;

import com.tcommerce.TCommerce.domain.entities.sales.ProcessedStripeEvent;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.ProcessedStripeEventEntity;
import org.springframework.stereotype.Component;

@Component
public class ProcessedStripeEventMapper {

    public ProcessedStripeEvent toDomain(ProcessedStripeEventEntity entity) {
        if (entity == null) return null;
        return ProcessedStripeEvent.builder()
                .eventId(entity.getEventId())
                .processedAt(entity.getProcessedAt())
                .build();
    }

    public ProcessedStripeEventEntity toEntity(ProcessedStripeEvent domain) {
        if (domain == null) return null;
        return ProcessedStripeEventEntity.builder()
                .eventId(domain.getEventId())
                .processedAt(domain.getProcessedAt())
                .build();
    }
}
