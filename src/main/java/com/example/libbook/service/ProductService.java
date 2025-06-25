package com.example.libbook.service;

import com.example.libbook.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByTag(String tag);
    List<Product> getNewArrivals(int limit);
    List<Product> getTopSellingProducts(int limit);
    Map<String, List<Product>> getProductCombosByRandomTags(int comboCount, int booksPerCombo);
}