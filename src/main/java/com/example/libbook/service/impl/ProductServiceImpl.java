package com.example.libbook.service.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProduct() {
        return productRepository.getAllProduct();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.getProductById(productId);
    }

    @Override
    public List<Product> getProductsByTag(String tag) {
        System.out.println("ProductServiceImpl: Calling getProductsByTag with tag: " + tag);
        return productRepository.getProductsByTag(tag);
    }

    @Override
    public void addProduct(Product product, List<Long> tagIds) throws IOException {
        System.out.println("ProductServiceImpl: Calling addProduct with name: " + product.getProductName());
        productRepository.addProduct(product, tagIds);
    }

    @Override
    public void updateProduct(Product product, List<Long> tagIds) {
        System.out.println("ProductServiceImpl: Calling updateProduct with id: " + product.getProductId());
        try {
            productRepository.updateProduct(product, tagIds);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void softDeleteProduct(Long productId) {
        System.out.println("ProductServiceImpl: Calling softDeleteProduct with id: " + productId);
        productRepository.softDeleteProduct(productId);
    }

    @Override
    public List<Product> getTopSellingProducts(int limit) {
        return productRepository.getTopSellingProducts(limit);
    }

    @Override
    public Map<String, List<Product>> getProductListByTag() {
        List<String> tags = productRepository.getAllTags();
        Map<String, List<Product>> result = new HashMap<>();

        for (String tag : tags) {
            List<Product> products = productRepository.getProductsByTag(tag);
            if (!products.isEmpty()) {
                result.put(tag, products);
            }
        }
        return result;
    }


}