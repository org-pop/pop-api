package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(UUID userId);

    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);

    Long countByStatus(OrderStatus status);
}