package com.example.webmarket.controller;

import com.example.webmarket.model.Product;
import com.example.webmarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Добавление нескольких продуктов
    @PostMapping("/add")
    public List<Product> addProducts(@RequestBody List<Product> products) {
        return productRepository.saveAll(products);
    }
    // Получение всех продуктов
    @CrossOrigin(origins = "*")
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}
