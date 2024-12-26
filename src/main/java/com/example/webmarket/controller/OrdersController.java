package com.example.webmarket.controller;

import com.example.webmarket.DTO.OrderDTO;
import com.example.webmarket.model.Orders;
import com.example.webmarket.services.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Orders>> getOrdersBySellerId(@PathVariable Long sellerId) {
        List<Orders> orders = ordersService.getOrdersBySellerId(sellerId);
        return ResponseEntity.ok(orders);
    }
}
