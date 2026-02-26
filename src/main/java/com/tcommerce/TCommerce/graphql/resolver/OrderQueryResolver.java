package com.tcommerce.TCommerce.graphql.resolver;

import com.tcommerce.TCommerce.application.query.OrderFilter;
import com.tcommerce.TCommerce.application.services.sales.OrderService;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.graphql.mapper.GraphQLMapper;
import com.tcommerce.TCommerce.graphql.util.CursorUtil;
import com.tcommerce.TCommerce.infrastructure.security.services.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderQueryResolver {

    private final OrderService orderService;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    @QueryMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, Object> managerOrders(
            @Argument Integer first,
            @Argument String after,
            @Argument OrderStatus status) {

        int limit = resolveLimit(first);
        ScrollPosition position = resolveScrollPosition(after);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        OrderFilter filter = new OrderFilter(status, null);
        Window<Order> window = orderService.getAllOrders(filter, position, limit, sort);

        return buildConnection(window, after != null && !after.isEmpty());
    }

    @QueryMapping
    @PreAuthorize("hasRole('CLIENT')")
    public Map<String, Object> myOrders(
            @Argument Integer first,
            @Argument String after,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        int limit = resolveLimit(first);
        ScrollPosition position = resolveScrollPosition(after);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        OrderFilter filter = new OrderFilter(null, userDetails.getId());
        Window<Order> window = orderService.getAllOrders(filter, position, limit, sort);

        return buildConnection(window, after != null && !after.isEmpty());
    }

    private int resolveLimit(Integer first) {
        if (first == null || first <= 0) return DEFAULT_PAGE_SIZE;
        return Math.min(first, MAX_PAGE_SIZE);
    }

    private ScrollPosition resolveScrollPosition(String after) {
        if (after == null || after.isEmpty()) {
            return ScrollPosition.keyset();
        }
        CursorUtil.DecodedCursor decoded = CursorUtil.decode(after);
        return ScrollPosition.forward(
                Map.of("createdAt", decoded.createdAt(), "id", decoded.id())
        );
    }


    private Map<String, Object> buildConnection(Window<Order> window, boolean hasPreviousPage) {
        List<Map<String, Object>> edges = window.getContent().stream()
                .map(order -> {
                    Map<String, Object> edge = new LinkedHashMap<>();
                    edge.put("cursor", CursorUtil.encode(order.getCreatedAt(), order.getId()));
                    edge.put("node", GraphQLMapper.toGraphQLOrder(order));
                    return edge;
                })
                .collect(Collectors.toList());

        Map<String, Object> pageInfo = new LinkedHashMap<>();
        pageInfo.put("hasNextPage", window.hasNext());
        pageInfo.put("hasPreviousPage", hasPreviousPage);

        if (!edges.isEmpty()) {
            pageInfo.put("startCursor", edges.get(0).get("cursor"));
            pageInfo.put("endCursor", edges.get(edges.size() - 1).get("cursor"));
        } else {
            pageInfo.put("startCursor", null);
            pageInfo.put("endCursor", null);
        }

        Map<String, Object> connection = new LinkedHashMap<>();
        connection.put("edges", edges);
        connection.put("pageInfo", pageInfo);
        return connection;
    }
}
