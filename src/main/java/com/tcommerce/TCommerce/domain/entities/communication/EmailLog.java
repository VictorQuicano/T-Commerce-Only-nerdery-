package com.tcommerce.TCommerce.domain.entities.communication;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {
    private String id;
    private String recipientEmail;
    private String subject;
    private String content;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
