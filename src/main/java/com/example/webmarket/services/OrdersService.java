package com.example.webmarket.services;

import com.example.webmarket.DTO.OrderDTO;
import com.example.webmarket.model.Cart;
import com.example.webmarket.model.Orders;
import com.example.webmarket.model.User;
import com.example.webmarket.repository.CartRepository;
import com.example.webmarket.repository.OrdersRepository;
import com.example.webmarket.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrdersService(OrdersRepository ordersRepository, UserRepository userRepository, CartRepository cartRepository) {
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
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

    public List<OrderDTO> getOrdersDTOBySellerId(Long sellerId) {
        List<Orders> orders = ordersRepository.findBySellerId(sellerId);
        return orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setData(order.getData());
            dto.setPrice(order.getPrice());
            dto.setStatus(order.getStatus());
            dto.setSellerId(order.getSeller().getId());
            dto.setUserId(order.getUser().getId());
            dto.setAmount(Long.valueOf(order.getAmount()));
            dto.setProductId(order.getProduct().getId());
            return dto;
        }).toList();
    }


    // Обработка оплаты: перенос из корзины в заказы и удаление из корзины
    @Transactional
    public void processPayment(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }

        User user = userOptional.get();

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста. Невозможно обработать оплату.");
        }

        for (Cart cartItem : cartItems) {
            Orders order = new Orders();
            order.setUser(user);
            order.setSeller(cartItem.getProduct().getUser());
            order.setProduct(cartItem.getProduct());
            order.setAmount(cartItem.getAmount());
            order.setPrice((float) (cartItem.getProduct().getPrice() * cartItem.getAmount()));
            order.setStatus("Confirmed");
            order.setData(LocalDate.now());

            ordersRepository.save(order);
        }

        cartRepository.deleteByUser(user);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        order.setStatus(newStatus);
        ordersRepository.save(order);
    }

}
