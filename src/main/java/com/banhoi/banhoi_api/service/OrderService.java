package com.banhoi.banhoi_api.service;

import com.banhoi.banhoi_api.model.CartItem;
import com.banhoi.banhoi_api.model.Order;
import com.banhoi.banhoi_api.model.OrderItem;
import com.banhoi.banhoi_api.model.User;
import com.banhoi.banhoi_api.repository.OrderItemRepository;
import com.banhoi.banhoi_api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order createOrder(User user, List<CartItem> cartItems, double total) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setProduct(cartItem.getProduct());
            return orderItem;
        }).collect(Collectors.toList());

        order = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);

        return order;
    }
}