package com.tcommerce.TCommerce.application.controllers.manager;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.application.query.OrderPaginationRequest;
import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.application.services.common.PageProcessor;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.tcommerce.TCommerce.interfaces.dto.sales.UpdateOrderStatus;



@RestController
@RequestMapping(ApiPaths.V1 + "/manager/orders")
@PreAuthorize("hasAnyRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerOrderController extends PageProcessor {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Window<OrderResponse>> getAllOrders(OrderPaginationRequest request) {
        OrderFilter filter = new OrderFilter(request.status(), request.userId());
        
        PaginationCriteria criteria = processRequest(request);

        ScrollPosition position = ScrollPosition.keyset();

        Sort sort = Sort.unsorted();
        if (request.sortBy() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.sortOrder()) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, request.sortBy());
        }

        if (sort.isUnsorted()) {
             sort = Sort.by("id").descending();
        }
        
        int limit = criteria.limit();
        
        Window<Order> result = orderService.getAllOrders(filter, position, limit, sort);
        
        Window<OrderResponse> response = result.map(Order::toResponse);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderWithHistory> updateOrderStatus(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable String orderId, @RequestBody @Valid UpdateOrderStatus request) {
        String userId = userDetails.getId();
        String reason = request.reason();
        Order order = orderService.updateOrderStatus(orderId, request.status(), userId, reason);
        return ResponseEntity.ok(order.toResponseWithHistory());
    }   
}