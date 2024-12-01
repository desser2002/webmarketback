package com.example.webmarket.DTO;

public class JwtResponse {
    private String token;
    private Long id; // Поле для хранения ID пользователя

    public JwtResponse(String token, Long id) {
        this.token = token;
        this.id = id;
    }

    // Геттеры и сеттеры
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
