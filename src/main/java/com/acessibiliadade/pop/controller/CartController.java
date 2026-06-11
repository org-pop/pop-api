package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.CartItem;
import com.acessibiliadade.pop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add/{productId}")
    public CartItem addItem(@PathVariable UUID userId,
                            @PathVariable Long productId,
                            @RequestParam Integer quantity) {
        return cartService.addItemToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable UUID userId) {
        return cartService.getUserCart(userId);
    }

    @PutMapping("/{userId}/item/{itemId}")
    public CartItem updateQuantity(@PathVariable UUID userId,
                                   @PathVariable Long itemId,
                                   @RequestParam Integer quantity) {
        return cartService.updateItemQuantity(userId, itemId, quantity);
    }

    @DeleteMapping("/{userId}/item/{itemId}")
    public void removeItem(@PathVariable UUID userId, @PathVariable Long itemId) {
        cartService.removeItemFromCart(userId, itemId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
    }
}