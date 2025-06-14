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
        return productRepository.getAllProduct();
    }

    @Override
    public Product getProductById(Long productId) {
        System.out.println("ProductServiceImpl: Calling getProductById with id: " + productId);
        return productRepository.getProductById(productId);
    }

    @Override
    public List<Product> getProductsByTag(String tag) {
        System.out.println("ProductServiceImpl: Calling getProductsByTag with tag: " + tag);
        return productRepository.getProductsByTag(tag);
    }

    @Override
    public void addProduct(Product product, List<Long> tagIds) {
        System.out.println("ProductServiceImpl: Calling addProduct with name: " + product.getProductName());
        productRepository.addProduct(product, tagIds);
    }

    @Override
    public void updateProduct(Product product, List<Long> tagIds) {
        System.out.println("ProductServiceImpl: Calling updateProduct with id: " + product.getProductId());
        productRepository.updateProduct(product, tagIds);
    }

    @Override
    public void softDeleteProduct(Long productId) {
        System.out.println("ProductServiceImpl: Calling softDeleteProduct with id: " + productId);
        productRepository.softDeleteProduct(productId);
    }
}