package com.tcommerce.TCommerce.application.services.sales;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.domain.entities.sales.ProcessedStripeEvent;
import com.tcommerce.TCommerce.domain.repositories.interfaces.sales.ProcessedStripeEventRepository;
import com.tcommerce.TCommerce.interfaces.dto.sales.CheckoutResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderItemResponse;
import com.tcommerce.TCommerce.application.services.sales.ShippingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderService orderService;
    private final ProcessedStripeEventRepository processedStripeEventRepository;
    private final ShippingService shippingService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public CheckoutResponse createPaymentIntent(String orderId, String userId) {
        Order order = orderService.getOrderById(orderId);

        // Verify ownership
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to checkout this order");
        }

        // Verify order status
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new RuntimeException("Order is not in a valid state for checkout. Current status: " + order.getStatus());
        }
        order = shippingService.reserveStock(order);

        // Calculate total from order items
        BigInteger totalAmount = order.getItems().stream()
                .map(item -> item.toResponse())
                .map(OrderItemResponse::subtotal)
                .reduce(BigInteger.ZERO, BigInteger::add);

        if (totalAmount.compareTo(BigInteger.ZERO) <= 0) {
            throw new RuntimeException("Order total must be greater than zero");
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(totalAmount.longValueExact())
                    .setCurrency("usd")
                    .putMetadata("orderId", orderId)
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            orderService.initiatePayment(orderId, paymentIntent.getId(), userId);

            return new CheckoutResponse(paymentIntent.getClientSecret());

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void handleWebhook(byte[] payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(new String(payload), sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed: {}", e.getMessage());
            throw new RuntimeException("Invalid Stripe webhook signature");
        } catch (Exception e) {
            log.error("Failed to parse Stripe webhook event: {}", e.getMessage());
            throw new RuntimeException("Failed to parse webhook event");
        }

        System.out.println("Stripe event saved: " + event.getId());
        System.out.println(event);
 

        // Idempotency check
        if (processedStripeEventRepository.existsByEventId(event.getId())) {
            log.info("Stripe event {} already processed, skipping", event.getId());
            return;
        }

        String eventType = event.getType();
        log.info("Processing Stripe event: {} (type: {})", event.getId(), eventType);

        switch (eventType) {
            case "payment_intent.succeeded" -> handlePaymentSuccess(event);
            case "payment_intent.payment_failed" -> handlePaymentFailure(event);
            default -> log.info("Unhandled Stripe event type: {}", eventType);
        }

        ProcessedStripeEvent processedEvent = ProcessedStripeEvent.builder()
                .eventId(event.getId())
                .processedAt(LocalDateTime.now())
                .build();
        processedStripeEventRepository.save(processedEvent);
    }

    
    private void handlePaymentSuccess(Event event) {

        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
    
        String orderId = paymentIntent.getMetadata().get("orderId");
        
        if (orderId == null) {
            log.error("Order not found for payment intent {}", event.getId());
            return;
        }

        Order order = orderService.updateOrderStatus(orderId, OrderStatus.PAID, "SYSTEM", "Payment confirmed via Stripe");
        shippingService.startShipping(order);

    }

    private void handlePaymentFailure(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new RuntimeException("Failed to deserialize payment intent from event"));

        Map<String, String> metadata = paymentIntent.getMetadata();
        String orderId = metadata.get("orderId");

        if (orderId == null) {
            log.error("No orderId found in payment intent metadata for event {}", event.getId());
            return;
        }

        log.info("Payment failed for order {}, payment intent {}", orderId, paymentIntent.getId());
        orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_FAILED, "SYSTEM", "Payment failed via Stripe");
    }
}
