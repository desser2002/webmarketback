package com.example.webmarket.services;

import com.example.webmarket.model.Cart;
import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addItem(User user, Product product, Integer amount) {
        Optional<Cart> existingCartItem = cartRepository.findByUserAndProduct(user, product);
        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            cart.setAmount(cart.getAmount() + amount);
            return cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setAmount(amount);
            return cartRepository.save(cart);
        }
    }

    public void removeItem(User user, Product product) {
        cartRepository.findByUserAndProduct(user, product)
                .ifPresent(cartRepository::delete);
    }

    public Cart updateAmount(User user, Product product, Integer amount) {
        Cart cart = cartRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setAmount(amount);
        return cartRepository.save(cart);
    }

    public List<Cart> findByUser(User user) {
        return cartRepository.findByUser(user);
    }
}
