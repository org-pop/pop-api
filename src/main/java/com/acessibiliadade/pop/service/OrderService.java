package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.enums.OrderStatus;
import com.acessibiliadade.pop.exception.BusinessException;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.*;
import com.acessibiliadade.pop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + userId));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new BusinessException("Não é possível criar um pedido a partir de um carrinho vazio");
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + product.getName());
            }
        }

        // Criar pedido
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        // Criar itens do pedido e atualizar estoque com pessimistic lock (evita TOCTOU)
        for (CartItem cartItem : cartItems) {
            Integer quantity = cartItem.getQuantity();
            Long productId = cartItem.getProduct().getId();

            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + productId));

            if (product.getStock() < quantity) {
                throw new BusinessException("Estoque insuficiente para o produto: " + product.getName());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);

            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            total = total.add(itemTotal);
        }

        // Atualizar total do pedido
        savedOrder.setTotal(total);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Limpar carrinho
        cartItemRepository.deleteByCart(cart);

        return updatedOrder;
    }

    public List<Order> getUserOrders(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Pedido já está cancelado");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Pedido com status " + order.getStatus() + " não pode ser cancelado");
        }

        // Restaurar estoque com pessimistic lock (evita corrida com checkouts concorrentes)
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Long productId = item.getProduct().getId();
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + productId));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}