package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.CartItem;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthorizationService authorizationService;

    @PostMapping("/{userId}/add/{productId}")
    public CartItem addItem(@PathVariable UUID userId,
                            @PathVariable Long productId,
                            @RequestParam Integer quantity) {
        authorizationService.assertOwnership(userId);
        return cartService.addItemToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return cartService.getUserCart(userId);
    }

    @PutMapping("/{userId}/item/{itemId}")
    public CartItem updateQuantity(@PathVariable UUID userId,
                                   @PathVariable Long itemId,
                                   @RequestParam Integer quantity) {
        authorizationService.assertOwnership(userId);
        return cartService.updateItemQuantity(userId, itemId, quantity);
    }

    @DeleteMapping("/{userId}/item/{itemId}")
    public void removeItem(@PathVariable UUID userId, @PathVariable Long itemId) {
        authorizationService.assertOwnership(userId);
        cartService.removeItemFromCart(userId, itemId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        cartService.clearCart(userId);
    }
}
