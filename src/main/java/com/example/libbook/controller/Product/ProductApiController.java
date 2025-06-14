package com.example.libbook.controller.Product;

import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        System.out.println("API: Fetching all products");
        return productService.getAllProduct();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        System.out.println("API: Fetching product with id: " + id);
        Product product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/products/by-tag")
    public ResponseEntity<List<Product>> getProductsByTag(@RequestParam("tag") String tag) {
        System.out.println("API: Fetching products with tag: " + tag);
        List<Product> products = productService.getProductsByTag(tag);
        return products != null && !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @RequestParam("tagIds") List<Long> tagIds) {
        System.out.println("API: Adding product with name: " + product.getProductName());
        product.setBuys(0);
        product.setUserId(2L);
        product.setStatus(1);
        product.setRating(0.0);
        productService.addProduct(product, tagIds);
        System.out.println("API: Product added with ID: " + product.getProductId());
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("available") int available,
            @RequestParam("userId") Long userId,
            @RequestParam("status") int status,
            @RequestParam("author") String author,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds) {
        System.out.println("API: Received update for product with id: " + id + ", status: " + status); // Debug
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            System.out.println("API: Product with id " + id + " not found");
            return ResponseEntity.notFound().build();
        }

        // Cập nhật thông tin sản phẩm
        existingProduct.setProductName(productName);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setAvailable(available);
        existingProduct.setUserId(userId);
        existingProduct.setStatus(status); // Cập nhật status từ yêu cầu
        existingProduct.setAuthor(author);

        // Xử lý file ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            existingProduct.setImageFile(imageFile.getOriginalFilename());
            // Có thể thêm logic lưu file vào server tại đây
        }

        productService.updateProduct(existingProduct, tagIds == null ? new ArrayList<>() : tagIds);
        System.out.println("API: Product updated successfully with ID: " + id + ", new status: " + existingProduct.getStatus());
        return ResponseEntity.ok(existingProduct);
    }

    @PatchMapping("/products/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable Long id) {
        System.out.println("API: Soft deleting product with id: " + id);
        try {
            productService.softDeleteProduct(id);
            return ResponseEntity.ok().build(); // Trả về 200 OK
        } catch (RuntimeException e) {
            System.err.println("API: Error soft deleting product: " + e.getMessage());
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy
        }
    }
}