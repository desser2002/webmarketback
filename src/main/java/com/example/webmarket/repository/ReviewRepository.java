package com.example.webmarket.repository;

import com.example.webmarket.model.Product;
import com.example.webmarket.model.Review;
import com.example.webmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId); // To get all reviews for a product

    Optional<Review> findByProductAndUser(Product product, User user); // To prevent multiple reviews from the same user
}
