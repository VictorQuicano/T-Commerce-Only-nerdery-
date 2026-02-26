package com.tcommerce.TCommerce.application.controllers.sales;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderHistoryResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.AccessDeniedException;

import com.tcommerce.TCommerce.domain.entities.sales.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.V1 + "/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(orderService.createOrderFromCart(userDetails.getId()).toResponse());
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<OrderResponse> orders = orderService.getUserOrders(userDetails.getId()).stream()
                .map(order -> order.toResponse())
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String orderId) {

        OrderResponse order = orderService.getOrderById(orderId).toResponse();
        
        if (!order.userId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
        
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderWithHistory> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String orderId, @RequestBody @Valid @NotBlank String reason) {
        String userId = userDetails.getId();
        Order order = orderService.getOrderById(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to cancel this order");
        }
        order = orderService.cancelOrder(order, userId, reason);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }
    @GetMapping("/{orderId}/history")
    public ResponseEntity<OrderWithHistory> getOrderHistory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable String orderId) {

        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }
}
