package com.tcommerce.TCommerce.infrastructure.persistence.mappers.communication;

import com.tcommerce.TCommerce.domain.entities.communication.EmailLog;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.communication.EmailLogEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailLogMapper {

    public EmailLog toDomain(EmailLogEntity entity) {
        if (entity == null) return null;
        return EmailLog.builder()
                .id(entity.getId())
                .recipientEmail(entity.getRecipientEmail())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public EmailLogEntity toEntity(EmailLog domain) {
        if (domain == null) return null;
        EmailLogEntity entity = EmailLogEntity.builder()
                .recipientEmail(domain.getRecipientEmail())
                .subject(domain.getSubject())
                .content(domain.getContent())
                .build();
        entity.setId(domain.getId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
