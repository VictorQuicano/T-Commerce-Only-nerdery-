package com.tcommerce.TCommerce.graphql.resolver;

import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.graphql.mapper.GraphQLMapper;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderMutationResolver {

    private final OrderService orderService;

    @MutationMapping
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> createOrderFromCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Order order = orderService.createOrderFromCart(userDetails.getId());
        return GraphQLMapper.toGraphQLOrder(order);
    }

    @MutationMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, Object> updateDeliveryStatus(
            @Argument String orderId,
            @Argument OrderStatus status,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Order order = orderService.updateOrderStatus(orderId, status, userDetails.getId(), "Updated via GraphQL");
        return GraphQLMapper.toGraphQLOrder(order);
    }
}
