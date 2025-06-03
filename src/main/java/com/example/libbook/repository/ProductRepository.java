package com.example.libbook.repository;

import com.example.libbook.entity.Product;
import java.util.List;

public interface ProductRepository {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByCategory(String category);
}