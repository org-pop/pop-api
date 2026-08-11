package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.OrderDTOs.OrderItemResponse;
import com.acessibiliadade.pop.dto.OrderDTOs.OrderResponse;
import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.model.Order;
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
    public OrderResponse createOrder(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return OrderResponse.from(orderService.createOrderFromCart(userId));
    }

    @GetMapping("/{userId}")
    public List<OrderResponse> getUserOrders(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return orderService.getUserOrders(userId).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{orderId}/details")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        assertOrderOwnership(order);
        return OrderResponse.from(order);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItemResponse> getOrderItems(@PathVariable Long orderId) {
        assertOrderOwnership(orderService.getOrderById(orderId));
        return orderService.getOrderItems(orderId).stream().map(OrderItemResponse::from).toList();
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(@PathVariable Long orderId,
                              @RequestParam OrderStatus status) {
        return OrderResponse.from(orderService.updateOrderStatus(orderId, status));
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
