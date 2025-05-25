package com.banhoi.banhoi_api.repository;

import com.banhoi.banhoi_api.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByUserId(Long userId);
}
