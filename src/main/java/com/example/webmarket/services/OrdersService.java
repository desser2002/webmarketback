package com.example.webmarket.services;

import com.example.webmarket.DTO.OrderDTO;
import com.example.webmarket.model.Orders;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.OrdersRepository;
import com.example.webmarket.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;

    public OrdersService(OrdersRepository ordersRepository, UserRepository userRepository) {
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
    }

    // Добавить новый заказ
    public Orders addOrder(OrderDTO orderDTO) {
        User seller = userRepository.findById(orderDTO.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Продавец не найден"));
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Покупатель не найден"));

        Orders order = new Orders();
        order.setData(orderDTO.getData());
        order.setPrice(orderDTO.getPrice());
        order.setStatus(orderDTO.getStatus());
        order.setSeller(seller);
        order.setUser(user);

        return ordersRepository.save(order);
    }

    // Получить заказ по ID
    public Optional<Orders> getOrderById(Long id) {
        return ordersRepository.findById(id);
    }

    // Получить заказы по ID продавца
    public List<Orders> getOrdersBySellerId(Long sellerId) {
        return ordersRepository.findBySellerId(sellerId);
    }
}