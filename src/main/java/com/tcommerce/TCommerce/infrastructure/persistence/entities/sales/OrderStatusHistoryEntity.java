package com.tcommerce.TCommerce.infrastructure.persistence.entities.sales;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

import lombok.*;

@Entity
@Table(name = "order_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private OrderStatus toStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = true)
    private UserEntity changedBy;

    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
