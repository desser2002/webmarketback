package com.example.webmarket.controller;

import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.ProductRepository;
import com.example.webmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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

    private static final String UPLOAD_DIR = "C:/Users/dell/webmarket/uploads/"; // Директория для сохранения файлов

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
        String name = (String) productData.get("name");
        Object priceObject = productData.get("price");
        Double price = null;

        if (priceObject instanceof Integer) {
            price = ((Integer) priceObject).doubleValue();
        } else if (priceObject instanceof Double) {
            price = (Double) priceObject;
        } else {
            throw new IllegalArgumentException("Invalid type for price. Expected Integer or Double.");
        }

        String username = (String) productData.get("username");
        String imageUrl = (String) productData.get("imageUrl");
        String description = (String) productData.get("description");

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
        if (description == null || description.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'description' is required.");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setDescription(description);
        product.setUser(userOptional.get());

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
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            String fileUri = "/api/products/images/" + fileName;
            return ResponseEntity.ok(Map.of("fileUri", fileUri));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }
    // Получение продукта по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + id);
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String imageName) {
        try {
            // Формируем полный путь к файлу
            Path uploadDir = Paths.get("C:/Users/dell/webmarket/uploads/"); // Укажите вашу директорию для загрузки
            Path filePath = uploadDir.resolve(imageName).normalize();

            // Логирование для отладки
            System.out.println("Requested file path: " + filePath.toAbsolutePath());

            // Проверяем существование файла
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                System.err.println("File not found or not readable: " + filePath.toAbsolutePath());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Загружаем ресурс
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(resource);

        } catch (IOException e) {
            // Логирование исключения
            System.err.println("Error accessing file: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}