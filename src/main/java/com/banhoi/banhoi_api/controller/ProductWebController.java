package com.banhoi.banhoi_api.controller;

import com.banhoi.banhoi_api.model.Product;
import com.banhoi.banhoi_api.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductWebController {
    private final ProductRepository productRepo;
    public ProductWebController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }
    @GetMapping
    public String getAllProducts(@RequestParam(value = "search",required = false) String search, Model model){
        List<Product> products;
        if(search != null&& !search.trim().isEmpty()){
            products = productRepo.findByNameContainingIgnoreCase(search);
        }
        else {
            products = productRepo.findAll();
        }
        model.addAttribute("productList", products);
        return "product-list";
    }
    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model){
        Product product = productRepo.findById(id).orElse(null);
        if(product == null){
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }
}
