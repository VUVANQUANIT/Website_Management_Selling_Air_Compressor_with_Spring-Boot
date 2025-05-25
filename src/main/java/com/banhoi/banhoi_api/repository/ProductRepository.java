package com.banhoi.banhoi_api.repository;

import com.banhoi.banhoi_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByNameContainingIgnoreCase(String search);
    Optional<Product> findById(Long id);
}
