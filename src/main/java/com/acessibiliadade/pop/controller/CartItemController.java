
package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.CartItem;
import com.acessibiliadade.pop.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping
    public CartItem create(@RequestParam Long cartId,
                           @RequestParam Long productId,
                           @RequestParam Integer quantity) {
        return cartItemService.createCartItem(cartId, productId, quantity);
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getByCart(@PathVariable Long cartId) {
        return cartItemService.getItemsByCart(cartId);
    }

    @PutMapping("/{cartItemId}")
    public CartItem updateQuantity(@PathVariable Long cartItemId,
                                   @RequestParam Integer quantity) {
        return cartItemService.updateQuantity(cartItemId, quantity);
    }

    @DeleteMapping("/{cartItemId}")
    public void delete(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
    }

    @DeleteMapping("/cart/{cartId}/clear")
    public void clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
    }
}