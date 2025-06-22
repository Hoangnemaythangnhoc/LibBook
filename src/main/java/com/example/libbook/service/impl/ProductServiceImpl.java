package com.example.libbook.service.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Product> getProductsByTag(String tag) {
        System.out.println("ProductServiceImpl: Calling getProductsByTag with tag: " + tag);
        List<Product> products = productRepository.getProductsByTag(tag);
        System.out.println("ProductServiceImpl: Retrieved " + (products != null ? products.size() : "null") + " products for tag");
        return products;
    }

    @Override
    public List<Product> getNewArrivals(int limit) {
        return productRepository.getNewArrivals(limit);
    }

    @Override
    public List<Product> getTopSellingProducts(int limit) {
        return productRepository.getTopSellingProducts(limit);
    }

    @Override
    public Map<String, List<Product>> getProductCombosByRandomTags(int comboCount, int booksPerCombo) {
        List<String> tags = productRepository.getRandomTags(comboCount);
        Map<String, List<Product>> combos = new HashMap<>();

        for (String tag : tags) {
            List<Product> taggedProducts = productRepository.getProductsByTag(tag);
            if (!taggedProducts.isEmpty()) {
                Collections.shuffle(taggedProducts);
                combos.put(tag, taggedProducts.subList(0, Math.min(booksPerCombo, taggedProducts.size())));
            }
        }
        return combos;
    }

}