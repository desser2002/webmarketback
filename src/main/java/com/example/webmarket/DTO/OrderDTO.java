package com.example.webmarket.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {

    private Long id;            // ID заказа
    private LocalDate data;     // Дата заказа
    private Float price;        // Цена заказа
    private String status;      // Статус заказа
    private Long sellerId;      // ID продавца
    private Long userId;        // ID покупателя
}
