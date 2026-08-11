package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.dto.CartDTOs.ProductSummary;
import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTOs {

    public record OrderResponse(
            Long id,
            BigDecimal total,
            OrderStatus status,
            LocalDateTime createdAt
    ) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(order.getId(), order.getTotal(), order.getStatus(), order.getCreatedAt());
        }
    }

    public record OrderItemResponse(
            Long id,
            ProductSummary product,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getId(),
                    ProductSummary.from(item.getProduct()),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
        }
    }
}
