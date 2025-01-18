package com.example.webmarket.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {
    private Long id;
    private LocalDate data;
    private Float price;
    private String status;
    private Long sellerId;
    private Long userId;
    private Long amount;
    private Long productId; // ID продукта
}

