package com.example.webmarket.DTO;

import lombok.Data;

@Data
public class UserDataRequest {
    private String username;
    private String password;
    private String role; // Добавлено поле role
}
