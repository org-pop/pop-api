package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.model.CartItem;
import com.acessibiliadade.pop.model.Product;

import java.math.BigDecimal;

public class CartDTOs {

    // Resumo do produto usado dentro de carrinho/pedido — evita repetir a entidade
    // inteira (e o back-reference pra Cart/Order, que quebrava serialização lazy).
    public record ProductSummary(Long id, String name, BigDecimal price, String imageUrl) {
        public static ProductSummary from(Product product) {
            return new ProductSummary(product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
        }
    }

    public record CartItemResponse(Long id, ProductSummary product, Integer quantity) {
        public static CartItemResponse from(CartItem item) {
            return new CartItemResponse(item.getId(), ProductSummary.from(item.getProduct()), item.getQuantity());
        }
    }
}
