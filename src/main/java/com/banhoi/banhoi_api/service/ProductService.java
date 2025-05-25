package com.banhoi.banhoi_api.service;

import com.banhoi.banhoi_api.model.Product;
import com.banhoi.banhoi_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
