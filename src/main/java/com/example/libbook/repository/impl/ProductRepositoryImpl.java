package com.example.libbook.repository.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getLong("ProductId"));
        product.setProductName(rs.getString("ProductName"));
        product.setDescription(rs.getString("Description"));
        product.setBuys(rs.getInt("Buys"));
        product.setAvailable(rs.getInt("Available"));
        product.setPrice(rs.getDouble("Price"));
        product.setImageFile(rs.getString("ImageFile"));
        product.setUserId(rs.getLong("UserId"));
        product.setStatus(rs.getInt("Status"));
        product.setRating(rs.getDouble("Rating"));
        product.setCreateAt(rs.getTimestamp("CreateAt"));
        return product;
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
    public List<Product> getProductsByTag(String tag) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM Product p " +
                "JOIN ProductTag pt ON p.ProductId = pt.ProductId " +
                "JOIN Tag t ON pt.TagId = t.TagId " +
                "WHERE t.TagName = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            System.out.println("Setting tag parameter: " + tag);
            statement.setString(1, tag);
            System.out.println("Executing query: " + sql + " with tag: " + tag);
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
                System.out.println("Number of products fetched for tag '" + tag + "': " + products.size());
            }
        } catch (Exception e) {
            System.err.println("Error fetching products by tag: " + e.getMessage());
            throw new RuntimeException("Error fetching products by tag", e);
        }
        return products;
    }

    @Override
    public List<Product> getNewArrivals(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product ORDER BY CreateAt DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching new arrivals", e);
        }
        return products;
    }

    @Override
    public List<Product> getTopSellingProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product ORDER BY Buys DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching top-selling products", e);
        }
        return products;
    }

    public List<String> getRandomTags(int limit) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT TOP (?) TagName FROM Tag ORDER BY NEWID()";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("TagName"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching random tags", e);
        }
        return tags;
    }


}