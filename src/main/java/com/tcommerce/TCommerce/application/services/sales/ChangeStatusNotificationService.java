package com.tcommerce.TCommerce.application.services.sales;

import org.springframework.stereotype.Component;

import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChangeStatusNotificationService {
    private final UserRepository userRepository;
    private final MailEventPublisher mailEventPublisher;

    public void notifyStatusChange(String orderId, OrderStatus fromStatus, OrderStatus toStatus, String userId, String reason) {
        userRepository.findById(userId).ifPresent(user -> {
            java.util.Map<String, String> params = new java.util.HashMap<>();
            params.put("first_name", user.getFirstName());
            params.put("order_id", orderId);
            params.put("from_status", fromStatus != null ? getFriendlyStatus(fromStatus) : "None");
            params.put("to_status", getFriendlyStatus(toStatus));
            params.put("changed_at", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            params.put("reason", reason != null ? reason : "");
            params.put("custom_message", getCustomMessage(toStatus));

            mailEventPublisher.buildAndPublish(
                user.getEmail(),
                "Order Update: " + orderId,
                "order-"+toStatus.name().toLowerCase(),
                params
            );
        });
    }

    private String getFriendlyStatus(OrderStatus status) {
        if (status == null) return "Unknown";
        String name = status.name().replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String getCustomMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Your order has been created and is currently under review.";
            case AWAITING_PAYMENT -> "We're waiting for your payment to be confirmed so we can start preparing your order.";
            case PAID -> "Excellent! We've received your payment and our team is already working on your order.";
            case PAYMENT_FAILED -> "There was an issue with the payment. Please review your payment details and try again.";
            case CANCELLED -> "Your order has been cancelled. If you didn't request this, please contact our support.";
            case SHIPPED -> "Exciting news! Your package has been shipped and is on its way to your destination.";
            case DELIVERED -> "Success! Your order has been delivered. We hope you enjoy your purchase!";
        };
    }
}
