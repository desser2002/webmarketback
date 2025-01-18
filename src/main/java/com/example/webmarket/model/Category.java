package com.example.webmarket.model;

import jakarta.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Getter for `id`
    public Long getId() {
        return id;
    }

    // Setter for `id`
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for `name`
    public String getName() {
        return name;
    }

    // Setter for `name`
    public void setName(String name) {
        this.name = name;
    }
}
