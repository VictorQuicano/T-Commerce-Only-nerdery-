package com.tcommerce.TCommerce.domain.repositories.interfaces.communication;

import com.tcommerce.TCommerce.domain.entities.communication.EmailLog;
import com.tcommerce.TCommerce.application.query.EmailLogFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailLogRepository {
    EmailLog save(EmailLog emailLog);
    Page<EmailLog> findAll(EmailLogFilter filter, Pageable pageable);
}
