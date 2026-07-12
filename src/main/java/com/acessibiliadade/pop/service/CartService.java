package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.exception.BusinessException;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.Cart;
import com.acessibiliadade.pop.model.CartItem;
import com.acessibiliadade.pop.model.Product;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.CartItemRepository;
import com.acessibiliadade.pop.repository.CartRepository;
import com.acessibiliadade.pop.repository.ProductRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public CartItem addItemToCart(UUID userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + productId));

        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        return cartItemRepository.save(newItem);
    }

    public List<CartItem> getUserCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public void removeItemFromCart(UUID userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Item não pertence ao carrinho do usuário");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public CartItem updateItemQuantity(UUID userId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            removeItemFromCart(userId, cartItemId);
            return null;
        }

        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Item não pertence ao carrinho do usuário");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart(cart);
    }
}