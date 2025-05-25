package com.banhoi.banhoi_api.repository;

import com.banhoi.banhoi_api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
