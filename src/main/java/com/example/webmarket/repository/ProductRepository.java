package com.example.webmarket.repository;

import com.example.webmarket.model.Category;
import com.example.webmarket.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category); // Метод для получения всех продуктов по категории

}
