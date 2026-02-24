package com.tcommerce.TCommerce.domain.repositories.implementations.sales;

import com.tcommerce.TCommerce.domain.entities.sales.ProcessedStripeEvent;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.ProcessedStripeEventRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.ProcessedStripeEventMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales.JpaProcessedStripeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedStripeEventRepositoryImpl implements ProcessedStripeEventRepository {

    private final JpaProcessedStripeEventRepository jpaRepository;
    private final ProcessedStripeEventMapper mapper;

    @Override
    public boolean existsByEventId(String eventId) {
        return jpaRepository.existsByEventId(eventId);
    }

    @Override
    public void save(ProcessedStripeEvent event) {
        jpaRepository.save(mapper.toEntity(event));
    }
}
