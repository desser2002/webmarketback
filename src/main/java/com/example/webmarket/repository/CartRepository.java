package com.example.webmarket.repository;

import com.example.webmarket.model.Cart;
import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);

}
