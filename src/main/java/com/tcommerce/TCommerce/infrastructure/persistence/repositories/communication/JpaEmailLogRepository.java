package com.tcommerce.TCommerce.infrastructure.persistence.repositories.communication;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.communication.EmailLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEmailLogRepository extends JpaRepository<EmailLogEntity, String>, JpaSpecificationExecutor<EmailLogEntity> {
}
