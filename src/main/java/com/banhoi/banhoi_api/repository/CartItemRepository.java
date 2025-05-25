package com.banhoi.banhoi_api.repository;

import com.banhoi.banhoi_api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
