package com.tcommerce.TCommerce.infrastructure.persistence.entities.communication;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLogEntity extends BaseEntity {

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public EmailLogEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, 
                          String recipientEmail, String subject, String content, UserEntity user) {
        super(id, createdAt, updatedAt);
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
        this.user = user;
    }
}
