package com.tcommerce.TCommerce.application.query;

import java.time.LocalDateTime;

public record EmailLogFilter(
    String userEmail,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}
