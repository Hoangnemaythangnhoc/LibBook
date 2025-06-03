package com.example.libbook.repository.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DataSource dataSource;

    @Autowired
    public ProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("ProductRepositoryImpl initialized with DataSource: " + (dataSource != null ? "Yes" : "No"));
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Executing query: " + sql);
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getLong("ProductId"));
                product.setProductName(resultSet.getString("ProductName"));
                product.setDescription(resultSet.getString("Description"));
                product.setBuys(resultSet.getInt("Buys"));
                product.setAvailable(resultSet.getInt("Available"));
                product.setPrice(resultSet.getDouble("Price"));
                product.setImageFile(resultSet.getString("ImageFile"));
                product.setUserId(resultSet.getLong("UserId"));
                product.setStatus(resultSet.getInt("Status"));
                product.setRating(resultSet.getDouble("Rating"));
                products.add(product);
            }
            System.out.println("Number of products fetched: " + products.size());
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            throw new RuntimeException("Error fetching products", e);
        }
        return products;
    }

    @Override
    public Product getProductById(Long productId) {
        String sql = "SELECT * FROM Product WHERE ProductId = ?";
        Product product = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, productId);
            System.out.println("Executing query: " + sql + " with productId: " + productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    product = new Product();
                    product.setProductId(resultSet.getLong("ProductId"));
                    product.setProductName(resultSet.getString("ProductName"));
                    product.setDescription(resultSet.getString("Description"));
                    product.setBuys(resultSet.getInt("Buys"));
                    product.setAvailable(resultSet.getInt("Available"));
                    product.setPrice(resultSet.getDouble("Price"));
                    product.setImageFile(resultSet.getString("ImageFile"));
                    product.setUserId(resultSet.getLong("UserId"));
                    product.setStatus(resultSet.getInt("Status"));
                    product.setRating(resultSet.getDouble("Rating"));
                }
                System.out.println("Product fetched: " + (product != null ? product.getProductName() : "null"));
            }
        } catch (Exception e) {
            System.err.println("Error fetching product by id: " + e.getMessage());
            throw new RuntimeException("Error fetching product by id", e);
        }
        return product;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE tag = ?"; // Giả định có cột 'tag' trong bảng Product

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category);
            System.out.println("Executing query: " + sql + " with category: " + category);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductId(resultSet.getLong("ProductId"));
                    product.setProductName(resultSet.getString("ProductName"));
                    product.setDescription(resultSet.getString("Description"));
                    product.setBuys(resultSet.getInt("Buys"));
                    product.setAvailable(resultSet.getInt("Available"));
                    product.setPrice(resultSet.getDouble("Price"));
                    product.setImageFile(resultSet.getString("ImageFile"));
                    product.setUserId(resultSet.getLong("UserId"));
                    product.setStatus(resultSet.getInt("Status"));
                    product.setRating(resultSet.getDouble("Rating"));
                    products.add(product);
                }
                System.out.println("Number of products fetched for category: " + products.size());
            }
        } catch (Exception e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
            throw new RuntimeException("Error fetching products by category", e);
        }
        return products;
    }
}