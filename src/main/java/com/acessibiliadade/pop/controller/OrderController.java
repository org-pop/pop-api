package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.OrderItem;
import com.acessibiliadade.pop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}/checkout")
    public Order createOrder(@PathVariable UUID userId) {
        return orderService.createOrderFromCart(userId);
    }

    @GetMapping("/{userId}")
    public List<Order> getUserOrders(@PathVariable UUID userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/{orderId}/details")
    public Order getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @PutMapping("/{orderId}/status")
    public Order updateStatus(@PathVariable Long orderId,
                              @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @DeleteMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }
}