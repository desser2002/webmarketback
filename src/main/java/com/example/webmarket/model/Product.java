package com.example.webmarket.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private Integer rating;
    private Integer reviewCount;
    private String imageUrl;
    private String discount;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Поле user_id в таблице product
    private User user; // Связь с пользователем
}
