package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrder(Order order);
}