package com.tcommerce.TCommerce.application.services.sales;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.sales.Order;
import com.tcommerce.TCommerce.domain.entities.sales.OrderItem;
import com.tcommerce.TCommerce.application.services.commerce.ProductService;
import com.tcommerce.TCommerce.application.services.commerce.StockNotificationService;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.entities.auth.User;

import org.springframework.stereotype.Service;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingService {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final StockNotificationService stockNotificationService;
    private final OrderService orderService;

    public Order reserveStock(Order order) {
        String userId = order.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.getItems().removeIf(item -> !checkStock(item, user));
        return orderService.save(order);
    }

    public void startShipping(Order order) {
        orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED, "SYSTEM", "Order shipped");
    }

    private boolean checkStock(OrderItem order, User user) {
        Product product = productService.getProductById(order.getProductId());
        if(product == null ){
            return false;
        }
        if (!productService.hasEnoughStock(product, BigInteger.valueOf(order.getQuantity()))){
            stockNotificationService.notifyUserNotEnoughStock(product, user);
            return false;
        }
        productService.reduceStock(product, BigInteger.valueOf(order.getQuantity()));
        return true;
    }
}
