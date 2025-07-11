package com.example.libbook.repository.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.entity.Tag;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DataSource dataSource;

    @Autowired
    private final ImageUtils imageUtils;

    @Autowired
    public ProductRepositoryImpl(DataSource dataSource, ImageUtils imageUtils) {
        this.dataSource = dataSource;
        this.imageUtils = imageUtils;
        System.out.println("ProductRepositoryImpl initialized with DataSource: " + (dataSource != null ? "Yes" : "No"));
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getLong("productId"));
                product.setProductName(resultSet.getString("productName"));
                product.setDescription(resultSet.getString("description"));
                product.setBuys(resultSet.getInt("buys"));
                product.setAvailable(resultSet.getInt("available"));
                product.setPrice(resultSet.getDouble("price"));
                product.setImageFile(resultSet.getString("imageFile"));
                product.setUserId(resultSet.getLong("userId"));
                product.setStatus(resultSet.getInt("status"));
                product.setRating(resultSet.getDouble("rating"));
                product.setAuthor(resultSet.getString("author"));
                product.setDiscount(resultSet.getInt("discount"));
                if (product.getStatus() == 1) {
                    products.add(product);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            throw new RuntimeException("Error fetching products", e);
        }
        return products;
    }

    @Override
    public Product getProductById(Long productId) {
        String sql = "SELECT * FROM product WHERE productId = ?";
        Product product = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    product = new Product();
                    product.setProductId(resultSet.getLong("productId"));
                    product.setProductName(resultSet.getString("productName"));
                    product.setDescription(resultSet.getString("description"));
                    product.setBuys(resultSet.getInt("buys"));
                    product.setAvailable(resultSet.getInt("available"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setImageFile(resultSet.getString("imageFile"));
                    product.setUserId(resultSet.getLong("userId"));
                    product.setStatus(resultSet.getInt("status"));
                    product.setRating(resultSet.getDouble("rating"));
                    product.setAuthor(resultSet.getString("author"));
                    product.setDiscount(resultSet.getInt("discount"));
                }
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

        String sql = "SELECT p.* FROM product p " +
                "JOIN ProductTag pt ON p.productId = pt.productId " +
                "JOIN Tag t ON pt.tagId = t.tagId " +
                "WHERE t.tagName = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductId(resultSet.getLong("productId"));
                    product.setProductName(resultSet.getString("productName"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setBuys(resultSet.getInt("buys"));
                    product.setImageFile(resultSet.getString("imageFile"));
                    product.setUserId(resultSet.getLong("userId"));
                    product.setAvailable(resultSet.getInt("available"));
                    product.setStatus(resultSet.getInt("status"));
                    product.setRating(resultSet.getDouble("rating"));
                    product.setAuthor(resultSet.getString("author"));
                    product.setDiscount(resultSet.getInt("discount"));
                    if (product.getStatus() == 1) {
                        products.add(product);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching products by tag: " + e.getMessage());
            throw new RuntimeException("Error fetching products by tag", e);
        }
        return products;
    }

    @Override

    public void addProduct(Product product, List<Long> tagIds) throws IOException {
        byte[] baseImage = imageUtils.decodeBase64(product.getImageFile());
        String image = imageUtils.uploadAvatar(baseImage,2);

    public void addProduct(Product product, List<Long> tagIds) {

        String sql = "INSERT INTO product (productName, description, price, imageFile, buys, available, userId, status, rating, author, discount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, image);
            statement.setInt(5, product.getBuys());
            statement.setInt(6, product.getAvailable());
            statement.setLong(7, product.getUserId());
            statement.setInt(8, product.getStatus());
            statement.setDouble(9, product.getRating());
            statement.setString(10, product.getAuthor());
            statement.setInt(11, product.getDiscount());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to add product, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getLong(1));
                }
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                String insertTagSql = "INSERT INTO ProductTag (productId, tagId) VALUES (?, ?)";
                try (PreparedStatement tagStatement = connection.prepareStatement(insertTagSql)) {
                    for (Long tagId : tagIds) {
                        tagStatement.setLong(1, product.getProductId());
                        tagStatement.setLong(2, tagId);
                        tagStatement.addBatch();
                    }
                    tagStatement.executeBatch();
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
            throw new RuntimeException("Error adding product", e);
        }
    }

    @Override
    public void updateProduct(Product product, List<Long> tagIds) {
        String sql = "UPDATE product SET productName = ?, description = ?, price = ?, imageFile = ?, buys = ?, available = ?, " +
                "userId = ?, status = ?, rating = ?, author = ?, discount = ? WHERE productId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getImageFile());
            statement.setInt(5, product.getBuys());
            statement.setInt(6, product.getAvailable());
            statement.setLong(7, product.getUserId());
            statement.setInt(8, product.getStatus());
            statement.setDouble(9, product.getRating());
            statement.setString(10, product.getAuthor());
            statement.setInt(11, product.getDiscount());
            statement.setLong(12, product.getProductId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update product, no rows affected.");
            }

            String deleteTagSql = "DELETE FROM ProductTag WHERE productId = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteTagSql)) {
                deleteStatement.setLong(1, product.getProductId());
                deleteStatement.executeUpdate();
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                String insertTagSql = "INSERT INTO ProductTag (productId, tagId) VALUES (?, ?)";
                try (PreparedStatement tagStatement = connection.prepareStatement(insertTagSql)) {
                    for (Long tagId : tagIds) {
                        tagStatement.setLong(1, product.getProductId());
                        tagStatement.setLong(2, tagId);
                        tagStatement.addBatch();
                    }
                    tagStatement.executeBatch();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void softDeleteProduct(Long productId) {
        String sql = "UPDATE product SET status = 0 WHERE productId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to soft delete product, no rows affected.");
            }
        } catch (Exception e) {
            System.err.println("Error soft deleting product: " + e.getMessage());
            throw new RuntimeException("Error soft deleting product", e);
        }
    }
}
