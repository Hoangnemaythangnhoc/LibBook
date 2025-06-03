package com.example.libbook.service;

import com.example.libbook.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByCategory(String category);
}