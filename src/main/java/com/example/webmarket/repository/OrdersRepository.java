package com.example.webmarket.repository;

import com.example.webmarket.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // Получить заказы по ID продавца
    List<Orders> findBySellerId(Long sellerId);
}
