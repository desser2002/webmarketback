package com.example.webmarket.controller;

import com.example.webmarket.DTO.OrderDTO;

import com.example.webmarket.model.Orders;
import com.example.webmarket.services.OrdersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // Эндпоинт для добавления нового заказа
    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody OrderDTO orderDTO) {
        Orders order = ordersService.addOrder(orderDTO);
        return ResponseEntity.ok(order);
    }

    // Эндпоинт для получения заказа по ID заказа
    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Эндпоинт для получения заказов по ID продавца
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersBySellerId(@PathVariable Long sellerId) {
        List<OrderDTO> orderDTOs = ordersService.getOrdersDTOBySellerId(sellerId); // Вызов нового метода сервиса
        return ResponseEntity.ok(orderDTOs);
    }


    @PostMapping("/processPayment")
    public ResponseEntity<?> processPayment(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is required.");
        }

        ordersService.processPayment(userId);
        return ResponseEntity.ok("Payment processed successfully");
    }

    // Эндпоинт для обновления статуса заказа
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("newStatus");
        if (newStatus == null || newStatus.isEmpty()) {
            return ResponseEntity.badRequest().body("New status is required");
        }

        try {
            ordersService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok("Order status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
