package com.example.libbook.service.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.repository.TagRepository;
import com.example.libbook.service.NotificationService;
import com.example.libbook.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

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
    public void updateProduct(Product product, List<Long> tagIds) throws IOException {
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

    @Override
    public Map<String, List<Product>> getProductCombosByRandomTags(int comboCount, int booksPerCombo) {
        return Map.of();
    }

    @Override
    public int[] importProducts(List<Map<String, Object>> products) {
        int successCount = 0;
        int failureCount = 0;

        for (Map<String, Object> productData : products) {
            try {
                // Validate required fields
                if (!productData.containsKey("BookName") || productData.get("BookName") == null || ((String) productData.get("BookName")).trim().isEmpty()) {
                    failureCount++;
                    continue;
                }
                Product product = new Product();
                product.setProductName((String) productData.get("BookName"));
                product.setDescription((String) productData.get("Description"));
                product.setPrice(parseDouble(productData.get("Price")));
                product.setImageFile((String) productData.get("ImageFile"));
                product.setAuthor((String) productData.get("Author"));
                product.setDiscount(parseInt(productData.get("Discount")));
                product.setPublisher((String) productData.get("Publisher"));
                product.setAvailable(parseInt(productData.get("AvailableQuantity")));
                product.setBuys(0); // Default value
                product.setUserId(parseLong(productData.get("userID"))); // Default userId, adjust as needed
                product.setStatus(1); // Default status (e.g., active)
                product.setRating(0.0); // Default rating
                List<Long> tags = tagRepository.getTagByTagName((String) productData.getOrDefault("Tags",""));
                productRepository.addProductFromCSV(product,tags);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                System.err.println("Error processing product: " + productData.get("BookName") + " - " + e.getMessage());
            }
        }

        return new int[]{successCount, failureCount};
    }

    private double parseDouble(Object value) {
        if (value == null) return 0.0;
        try {
            // Handle price with currency symbol, e.g., "$119.60"
            String strValue = value.toString().replaceAll("[^\\d.]", "");
            return Double.parseDouble(strValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(Object value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long parseLong(Object value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


}