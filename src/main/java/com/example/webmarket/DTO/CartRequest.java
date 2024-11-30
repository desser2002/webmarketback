package com.example.webmarket.DTO;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Long productId;
    private Integer amount;
}
