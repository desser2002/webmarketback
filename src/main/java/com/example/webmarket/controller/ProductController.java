package com.example.webmarket.controller;

import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.ProductRepository;
import com.example.webmarket.repository.UserRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = "uploads"; // Директория для сохранения файлов
    @Autowired
    private UserRepository userRepository;

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
    @PostMapping("/new")
    public ResponseEntity<?> addNewProduct(@RequestBody Map<String, Object> productData) {
        // Извлечение данных из JSON
        String name = (String) productData.get("name");
        Object priceObject = productData.get("price");
        Double price = null;

        if (priceObject instanceof Integer) {
            price = ((Integer) priceObject).doubleValue(); // Преобразуем Integer в Double
        } else if (priceObject instanceof Double) {
            price = (Double) priceObject; // Используем значение, если это уже Double
        } else {
            throw new IllegalArgumentException("Invalid type for price. Expected Integer or Double.");
        }

        String username = (String) productData.get("username");
        String imageUrl = (String) productData.get("imageUrl"); // Извлекаем URL изображения

        // Проверка обязательных параметров
        if (name == null || name.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'name' is required.");
        }
        if (price == null || price <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'price' must be greater than 0.");
        }
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'username' is required.");
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'imageUrl' is required.");
        }

        // Проверка существования пользователя
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        // Создание нового продукта
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl); // Устанавливаем URL изображения
        product.setUser(userOptional.get());

        // Сохранение продукта
        Product savedProduct = productRepository.save(product);

        return ResponseEntity.ok("New product added");
    }

    // Загрузка изображения продукта
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            // Создание директории для загрузки файлов, если она не существует
            Path uploadPath = Paths.get("C:/Users/dell/webmarket/uploads/");
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
            return ResponseEntity.ok(Map.of("filePath", filePath.toAbsolutePath()));

        } catch (IOException e) {
            System.err.println("Error while saving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }
}
