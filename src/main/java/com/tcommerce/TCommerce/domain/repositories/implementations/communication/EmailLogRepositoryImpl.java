package com.tcommerce.TCommerce.domain.repositories.implementations.communication;

import com.tcommerce.TCommerce.application.query.EmailLogFilter;
import com.tcommerce.TCommerce.domain.entities.communication.EmailLog;
import com.tcommerce.TCommerce.domain.repositories.interfaces.communication.EmailLogRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.communication.EmailLogEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.communication.EmailLogMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.communication.JpaEmailLogRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.auth.JpaUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailLogRepositoryImpl implements EmailLogRepository {

    private final JpaEmailLogRepository jpaEmailLogRepository;
    private final JpaUserRepository jpaUserRepository;
    private final EmailLogMapper emailLogMapper;

    @Override
    public EmailLog save(EmailLog emailLog) {
        EmailLogEntity entity = emailLogMapper.toEntity(emailLog);
        if (emailLog.getUserId() != null) {
            entity.setUser(jpaUserRepository.getReferenceById(emailLog.getUserId()));
        }
        EmailLogEntity saved = jpaEmailLogRepository.save(entity);
        return emailLogMapper.toDomain(saved);
    }

    @Override
    public Page<EmailLog> findAll(EmailLogFilter filter, Pageable pageable) {
        Specification<EmailLogEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.userEmail() != null && !filter.userEmail().isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("email"), filter.userEmail()));
            }

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.endDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jpaEmailLogRepository.findAll(spec, pageable).map(emailLogMapper::toDomain);
    }
}
