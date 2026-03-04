package com.tcommerce.TCommerce.interfaces.dto.communication;

import java.time.LocalDateTime;

public record EmailLogResponse(
    String id,
    String recipientEmail,
    String subject,
    String content,
    String userId,
    LocalDateTime createdAt
) {}
