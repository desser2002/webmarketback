package com.example.webmarket.controller;

import com.example.webmarket.model.Category;
import com.example.webmarket.model.Product;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.CategoryRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = "C:/Users/dell/webmarket/uploads/";

    @PostMapping("/add")
    public List<Product> addProducts(@RequestBody List<Product> products) {
        return productRepository.saveAll(products);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/new")
    public ResponseEntity<?> addNewProduct(@RequestBody Map<String, Object> productData) {
        String name = (String) productData.get("name");
        Double price = ((Number) productData.get("price")).doubleValue();
        String username = (String) productData.get("username");
        String imageUrl = (String) productData.get("imageUrl");
        String description = (String) productData.get("description");
        Long categoryId = ((Number) productData.get("categoryId")).longValue();

        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Parameter 'name' is required.");
        }
        if (price == null || price <= 0) {
            return ResponseEntity.badRequest().body("Parameter 'price' must be greater than 0.");
        }
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Parameter 'username' is required.");
        }
        if (categoryId == null) {
            return ResponseEntity.badRequest().body("Parameter 'categoryId' is required.");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with ID: " + categoryId);
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setDescription(description);
        product.setUser(userOptional.get());
        product.setCategory(categoryOptional.get());

        productRepository.save(product);
        return ResponseEntity.ok(Map.of("message", "New product added successfully."));
    }

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
            Path uploadDir = Paths.get(UPLOAD_DIR);
            Path filePath = uploadDir.resolve(imageName).normalize();

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategoryName(@PathVariable String categoryName) {
        Optional<Category> categoryOptional = categoryRepository.findByNameIgnoreCase(categoryName); // Используем метод без учета регистра

        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Если категория не найдена
        }

        Category category = categoryOptional.get();
        List<Product> products = productRepository.findByCategory(category); // Предполагаем, что у вас есть метод для поиска продуктов по категории
        return ResponseEntity.ok(products);
    }
}
