package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.OrderItem;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthorizationService authorizationService;

    @PostMapping("/{userId}/checkout")
    public Order createOrder(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return orderService.createOrderFromCart(userId);
    }

    @GetMapping("/{userId}")
    public List<Order> getUserOrders(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/{orderId}/details")
    public Order getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        assertOrderOwnership(order);
        return order;
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        assertOrderOwnership(orderService.getOrderById(orderId));
        return orderService.getOrderItems(orderId);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateStatus(@PathVariable Long orderId,
                              @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @DeleteMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId) {
        assertOrderOwnership(orderService.getOrderById(orderId));
        orderService.cancelOrder(orderId);
    }

    private void assertOrderOwnership(Order order) {
        UUID current = authorizationService.currentUser().getId();
        if (order.getUser() == null || !current.equals(order.getUser().getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este pedido");
        }
    }
}
