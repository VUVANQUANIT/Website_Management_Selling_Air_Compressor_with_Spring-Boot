package com.banhoi.banhoi_api.repository;

import com.banhoi.banhoi_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
