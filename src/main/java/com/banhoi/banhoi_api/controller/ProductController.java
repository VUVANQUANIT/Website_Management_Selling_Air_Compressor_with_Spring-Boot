package com.banhoi.banhoi_api.controller;

import com.banhoi.banhoi_api.model.Product;
import com.banhoi.banhoi_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepo;
    @GetMapping
    public List<Product> getAllProducts(){
        return  productRepo.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id){
        return productRepo.findById(id).orElse(null);
    }
    @PostMapping
    public Product addProduct(@RequestBody Product product){
        return productRepo.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updateProduct){
        Optional<Product> productOptional = productRepo.findById(id);
        if(productOptional.isPresent()){
            Product product = productOptional.get();
            product.setName(updateProduct.getName());
            product.setPrice(updateProduct.getPrice());
            product.setDescription(updateProduct.getDescription());
            product.setStock(updateProduct.getStock());
            product.setImageUrl(updateProduct.getImageUrl());
            return productRepo.save(product);
        }
        else {
            return null;
        }
    }
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id){
        productRepo.deleteById(id);
    }

}
