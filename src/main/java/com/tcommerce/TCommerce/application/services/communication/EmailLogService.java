package com.tcommerce.TCommerce.application.services.communication;

import com.tcommerce.TCommerce.application.query.EmailLogFilter;
import com.tcommerce.TCommerce.domain.entities.communication.EmailLog;
import com.tcommerce.TCommerce.domain.repositories.interfaces.communication.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailLogService {

    private final EmailLogRepository emailLogRepository;

    public void logEmail(String recipientEmail, String subject, String content, String userId) {
        EmailLog log = EmailLog.builder()
                .id(UUID.randomUUID().toString())
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .userId(userId)
                .build();
        emailLogRepository.save(log);
    }

    public Page<EmailLog> getEmailLogs(EmailLogFilter filter, Pageable pageable) {
        return emailLogRepository.findAll(filter, pageable);
    }
}
