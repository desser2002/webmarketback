package com.example.webmarket.controller;

import com.example.webmarket.model.Review;
import com.example.webmarket.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@RequestBody ReviewRequest reviewRequest) {

        if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            return ResponseEntity.badRequest().body(null);
        }

        Review review = reviewService.addReview(
                reviewRequest.getProductId(),
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );
        return ResponseEntity.ok(review);
    }

    // DTO для JSON-запроса
    public static class ReviewRequest {
        private Long productId;
        private Long userId;
        private Integer rating;
        private String comment;

        // Getters and setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }


    @GetMapping("/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
}
