package com.banhoi.banhoi_api.service;

import com.banhoi.banhoi_api.model.Cart;
import com.banhoi.banhoi_api.model.CartItem;
import com.banhoi.banhoi_api.model.Product;
import com.banhoi.banhoi_api.model.User;
import com.banhoi.banhoi_api.repository.CartItemRepository;
import com.banhoi.banhoi_api.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    public void addToCart(Cart cart, Product product, int quantity) {
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setPrice(product.getPrice() * item.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice() * quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    public void removeFromCart(Cart cart, Long productId) {
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    public double getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getPrice())
                .sum();
    }
}