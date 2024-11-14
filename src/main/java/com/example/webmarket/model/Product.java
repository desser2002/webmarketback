package com.example.webmarket.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
@Data // Lombok аннотация для генерации геттеров и сеттеров
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
}
