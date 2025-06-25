package com.example.libbook.repository;

import com.example.libbook.entity.Product;
import java.util.List;

public interface ProductRepository {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByTag(String tag);
    List<Product> getNewArrivals(int limit);
    List<Product> getTopSellingProducts(int limit);
    List<String> getRandomTags(int limit);
}