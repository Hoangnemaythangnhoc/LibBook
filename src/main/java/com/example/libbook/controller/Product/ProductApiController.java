package com.example.libbook.controller.Product;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.Product;
import com.example.libbook.service.CheckBuyService;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CheckBuyService checkBuyService;

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
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product,
            @RequestParam("tagIds") List<Long> tagIds,
            @RequestParam(value = "discount", required = false, defaultValue = "0") int discount) throws IOException {
        System.out.println("API: Adding product with name: " + product.getProductName() + ", publisher: " + product.getPublisher());
        product.setBuys(0);
        product.setUserId(2L);
        product.setStatus(1);
        product.setRating(0.0);
        product.setDiscount(discount);

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            return ResponseEntity.badRequest().build();
        }

        // Làm sạch publisher để loại bỏ duplicate
        if (product.getPublisher() != null) {
            String[] publishers = product.getPublisher().split(",");
            product.setPublisher(String.join(",", new HashSet<String>() {{
                for (String p : publishers) {
                    add(p.trim());
                }
            }}));
        }

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
            @RequestParam("author") String author,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("base64") String baseImage,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "discount", required = false, defaultValue = "0") int discount) throws IOException {
        System.out.println("API: Received update for product with id: " + id + ", publisher: " + publisher);
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            System.out.println("API: Product with id " + id + " not found");
            return ResponseEntity.notFound().build();
        }

        if (discount < 0 || discount > 100) {
            return ResponseEntity.badRequest().build();
        }

        // Làm sạch publisher để loại bỏ duplicate
        if (publisher != null) {
            String[] publishers = publisher.split(",");
            publisher = String.join(",", new HashSet<String>() {{
                for (String p : publishers) {
                    add(p.trim());
                }
            }});
        } else {
            publisher = existingProduct.getPublisher();
        }

        existingProduct.setProductName(productName);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setAvailable(available);
        existingProduct.setUserId(userId);
        existingProduct.setStatus(1);
        existingProduct.setAuthor(author);
        existingProduct.setPublisher(publisher);
        existingProduct.setDiscount(discount);
        existingProduct.setImageFile(baseImage);

        productService.updateProduct(existingProduct, tagIds == null ? new ArrayList<>() : tagIds);
        System.out.println("API: Product updated successfully with ID: " + id);
        return ResponseEntity.ok(existingProduct);
    }

    @PatchMapping("/products/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable Long id) {
        System.out.println("API: Soft deleting product with id: " + id);
        try {
            productService.softDeleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.err.println("API: Error soft deleting product: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/products/checkbuy/status")
    public ResponseEntity<Boolean> checkBuyStatus(
            @RequestParam("productId") int productId,
            HttpSession session) {
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        if (currentUser == null) {
            return ResponseEntity.ok(false);
        }
        System.out.println("API: Checking CheckBuy status for UserId=" + currentUser.getUserId() + ", ProductId=" + productId);
        boolean hasReviewed = checkBuyService.hasUserReviewed(currentUser.getUserId(), productId);
        return ResponseEntity.ok(hasReviewed);
    }

    @GetMapping("/product/top-sale")
    public ResponseEntity<List<Product>> getTopSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        List<Product> products = productService.getTopSellingProducts(limit);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }

    @GetMapping("/product/list-by-tag")
    public ResponseEntity<Map<String, List<Product>>> getProductListByTag() {
        Map<String, List<Product>> result = productService.getProductListByTag();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }


}