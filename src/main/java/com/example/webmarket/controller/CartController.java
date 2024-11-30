package com.example.webmarket.controller;

import com.example.webmarket.DTO.CartRequest;
import com.example.webmarket.model.Cart;
import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;

import com.example.webmarket.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(@RequestBody CartRequest cartRequest) {
        User user = new User();
        user.setId(cartRequest.getUserId());

        Product product = new Product();
        product.setId(cartRequest.getProductId());

        Cart cart = cartService.addItem(user, product, cartRequest.getAmount());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(@RequestParam Long userId, @RequestParam Long productId) {
        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(productId);

        cartService.removeItem(user, product);
        return ResponseEntity.ok("Item removed successfully");
    }

    @PatchMapping("/update")
    public ResponseEntity<Cart> updateAmount(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer amount) {
        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(productId);

        Cart cart = cartService.updateAmount(user, product, amount);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getUserCart(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);

        List<Cart> cartItems = cartService.findByUser(user);
        return ResponseEntity.ok(cartItems);
    }
}
