package com.example.webmarket.controller;

import com.example.webmarket.model.Product;
import com.example.webmarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = "uploads"; // Директория для сохранения файлов

    // Добавление нескольких продуктов
    @PostMapping("/add")
    public List<Product> addProducts(@RequestBody List<Product> products) {
        return productRepository.saveAll(products);
    }

    // Получение всех продуктов
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Загрузка изображения продукта
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            // Создание директории для загрузки файлов, если она не существует
            Path uploadPath = Path.of("C:\\Users\\dell\\webmarket\\uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            // Генерация уникального имени файла
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Сохранение файла
            file.transferTo(filePath.toFile());
            System.out.println("File saved to: " + filePath.toAbsolutePath());

            // Возврат пути к загруженному файлу
            String fileUri = "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("filePath", fileUri));

        } catch (IOException e) {
            System.err.println("Error while saving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }
}
