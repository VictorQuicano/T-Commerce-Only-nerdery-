package com.tcommerce.TCommerce.application.services.sales;

import org.springframework.stereotype.Component;

import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.OrderRepository;

import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefundNotification {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MailEventPublisher mailEventPublisher;

    public void notifyRefund(String orderId, String userId, String reason) {
        userRepository.findById(userId).ifPresent(user -> {
            java.util.Map<String, String> params = new java.util.HashMap<>();
            params.put("first_name", user.getFirstName());
            params.put("order_id", orderId);
            params.put("reason", reason);
            mailEventPublisher.buildAndPublish(
                user.getEmail(),
                "Refund Update: " + orderId,
                "refund",
                params
            );
        });
    }
}
