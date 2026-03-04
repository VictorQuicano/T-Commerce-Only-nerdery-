package com.tcommerce.TCommerce.domain.entities.sales;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedStripeEvent {
    private String eventId;
    private LocalDateTime processedAt;
}
