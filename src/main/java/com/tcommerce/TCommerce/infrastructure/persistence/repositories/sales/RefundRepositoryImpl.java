package com.tcommerce.TCommerce.infrastructure.persistence.repositories.sales;

import com.tcommerce.TCommerce.domain.entities.sales.Refund;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.RefundRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.sales.RefundEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.sales.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RefundRepositoryImpl implements RefundRepository {

    private final JpaRefundRepository jpaRefundRepository;
    private final RefundMapper refundMapper;

    @Override
    public Refund save(Refund refund) {
        RefundEntity entity = refundMapper.toEntity(refund);
        RefundEntity savedEntity = jpaRefundRepository.save(entity);
        return refundMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Refund> findById(String id) {
        return jpaRefundRepository.findById(id)
                .map(refundMapper::toDomain);
    }

    @Override
    public List<Refund> findByOrderId(String orderId) {
        return jpaRefundRepository.findByOrder_Id(orderId).stream()
                .map(refundMapper::toDomain)
                .collect(Collectors.toList());
    }
}
