package com.tcommerce.TCommerce.graphql.mapper;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps domain entities to GraphQL-friendly Map representations
 * matching the schema types.
 */
public class GraphQLMapper {

    private GraphQLMapper() {}

    public static Map<String, Object> toGraphQLOrder(Order order) {
        List<Map<String, Object>> items = order.getItems().stream()
                .map(GraphQLMapper::toGraphQLOrderItem)
                .collect(Collectors.toList());

        BigInteger total = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigInteger.valueOf(item.getQuantity())))
                .reduce(BigInteger.ZERO, BigInteger::add);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getId());
        map.put("userId", order.getUserId());
        map.put("items", items);
        map.put("total", total.toString());
        map.put("status", order.getStatus().name());
        map.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        map.put("updatedAt", order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null);
        return map;
    }

    public static Map<String, Object> toGraphQLOrderItem(OrderItem item) {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("id", item.getProductId());
        product.put("name", item.getProductName());
        product.put("description", null);
        product.put("price", item.getPrice().toString());
        product.put("stock", 0);
        product.put("category", null);
        product.put("disabled", false);
        product.put("createdAt", item.getCreatedAt() != null ? item.getCreatedAt().toString() : "");
        map.put("product", product);
        map.put("quantity", item.getQuantity());
        map.put("price", item.getPrice().toString());
        return map;
    }

    public static Map<String, Object> toGraphQLCart(Cart cart) {
        List<Map<String, Object>> items = cart.getItems().stream()
                .map(GraphQLMapper::toGraphQLCartItem)
                .collect(Collectors.toList());

        BigInteger total = cart.getItems().stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigInteger.valueOf(ci.getQuantity())))
                .reduce(BigInteger.ZERO, BigInteger::add);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", cart.getId());
        map.put("items", items);
        map.put("total", total.toString());
        return map;
    }

    public static Map<String, Object> toGraphQLCartItem(CartItem item) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("product", toGraphQLProduct(item.getProduct()));
        map.put("quantity", item.getQuantity());
        return map;
    }

    public static Map<String, Object> toGraphQLProduct(Product product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("description", product.getDescription());
        map.put("price", product.getPrice().toString());
        map.put("stock", product.getStock() != null ? product.getStock().getQuantity().intValue() : 0);

        if (product.getCategory() != null) {
            Map<String, Object> category = new LinkedHashMap<>();
            category.put("id", product.getCategory().getId());
            category.put("name", product.getCategory().getName());
            map.put("category", category);
        } else {
            map.put("category", null);
        }

        map.put("disabled", !product.isActive());
        map.put("createdAt", product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        return map;
    }
}
