package com.example.webmarket.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating; // Rating from 1 to 5

    @Column(nullable = false, length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who left the review
}
