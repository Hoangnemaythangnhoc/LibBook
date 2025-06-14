package com.example.libbook.service;

import com.example.libbook.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByTag(String tag);
    void addProduct(Product product, List<Long> tagIds);
    void updateProduct(Product product, List<Long> tagIds);
    void softDeleteProduct(Long productId); // Thêm phương thức soft delete
}