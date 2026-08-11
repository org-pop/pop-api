package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.CartDTOs.CartItemResponse;
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
    public CartItemResponse addItem(@PathVariable UUID userId,
                            @PathVariable Long productId,
                            @RequestParam Integer quantity) {
        authorizationService.assertOwnership(userId);
        return CartItemResponse.from(cartService.addItemToCart(userId, productId, quantity));
    }

    @GetMapping("/{userId}")
    public List<CartItemResponse> getCart(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return cartService.getUserCart(userId).stream().map(CartItemResponse::from).toList();
    }

    @PutMapping("/{userId}/item/{itemId}")
    public CartItemResponse updateQuantity(@PathVariable UUID userId,
                                   @PathVariable Long itemId,
                                   @RequestParam Integer quantity) {
        authorizationService.assertOwnership(userId);
        return CartItemResponse.from(cartService.updateItemQuantity(userId, itemId, quantity));
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
