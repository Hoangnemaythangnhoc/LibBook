package com.example.libbook.controller.Product;

import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        System.out.println("API: Fetching all products");
        List<Product> products = productService.getAllProduct();
        System.out.println("API: Retrieved " + (products != null ? products.size() : "null") + " products");
        return products;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        System.out.println("API: Fetching product with id: " + id);
        Product product = productService.getProductById(id);
        System.out.println("API: Retrieved product: " + (product != null ? product.getProductName() : "null"));
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/products/by-tag")
    public ResponseEntity<List<Product>> getProductsByTag(@RequestParam("tag") String tag) {
        System.out.println("API: Fetching products with tag: " + tag);
        List<Product> products = productService.getProductsByTag(tag);
        System.out.println("API: Retrieved " + (products != null ? products.size() : "null") + " products for tag: " + tag);
        if (products == null || products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
}