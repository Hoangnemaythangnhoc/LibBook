package com.example.libbook.repository;

import com.example.libbook.entity.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ProductRepository {
    List<Product> getAllProduct();
    Product getProductById(Long productId);
    List<Product> getProductsByTag(String tag);
    Long addProduct(Product product, List<Long> tagIds) throws IOException;
    void updateProduct(Product product, List<Long> tagIds) throws IOException, SQLException;
    void softDeleteProduct(Long productId); // Thêm phương thức soft delete
    List<Product> getTopSellingProducts(int limit);
    List<String> getRandomTags(int limit);
    List<Product> getAllDataChart();
    List<String> getAllTags();
    Long addProductFromCSV(Product product, List<Long> tagIds);
    void updateQuantityProduct(int productID,int quantity);
}