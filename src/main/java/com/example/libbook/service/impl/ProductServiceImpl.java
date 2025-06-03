package com.example.libbook.service.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProduct() {
        System.out.println("ProductServiceImpl: Calling getAllProduct");
        List<Product> products = productRepository.getAllProduct();
        System.out.println("ProductServiceImpl: Retrieved " + (products != null ? products.size() : "null") + " products");
        return products;
    }

    @Override
    public Product getProductById(Long productId) {
        System.out.println("ProductServiceImpl: Calling getProductById with id: " + productId);
        Product product = productRepository.getProductById(productId);
        System.out.println("ProductServiceImpl: Retrieved product: " + (product != null ? product.getProductName() : "null"));
        return product;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        System.out.println("ProductServiceImpl: Calling getProductsByCategory with category: " + category);
        List<Product> products = productRepository.getProductsByCategory(category);
        System.out.println("ProductServiceImpl: Retrieved " + (products != null ? products.size() : "null") + " products for category");
        return products;
    }
}