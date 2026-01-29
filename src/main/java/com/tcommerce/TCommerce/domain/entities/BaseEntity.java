package com.tcommerce.TCommerce.domain.entities;

import java.time.LocalDateTime;

public interface BaseEntity {
    String getId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
