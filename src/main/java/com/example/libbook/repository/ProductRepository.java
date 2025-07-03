package com.example.libbook.repository;

import com.example.libbook.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ProductRepository {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByTag(String tag);
    void addProduct(Product product, List<Long> tagIds) throws IOException;
    void updateProduct(Product product, List<Long> tagIds);
    void softDeleteProduct(Long productId); // Thêm phương thức soft delete
    List<Product> getNewArrivals(int limit);
    List<Product> getTopSellingProducts(int limit);
    List<String> getRandomTags(int limit);
}