package com.example.libbook.repository.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.entity.Tag;
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
                product.setAuthor(resultSet.getString("Author"));
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
        String sql = "SELECT * FROM Product WHERE ProductId = ?";
        Product product = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
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
                    product.setAuthor(resultSet.getString("Author"));
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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tag);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductId(resultSet.getLong("ProductId"));
                    product.setProductName(resultSet.getString("ProductName"));
                    product.setDescription(resultSet.getString("Description"));
                    product.setPrice(resultSet.getDouble("Price"));
                    product.setBuys(resultSet.getInt("Buys"));
                    product.setImageFile(resultSet.getString("ImageFile"));
                    product.setUserId(resultSet.getLong("UserId"));
                    product.setAvailable(resultSet.getInt("Available"));
                    product.setStatus(resultSet.getInt("Status"));
                    product.setRating(resultSet.getDouble("Rating"));
                    product.setAuthor(resultSet.getString("Author"));
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
    public void addProduct(Product product, List<Long> tagIds) {
        String sql = "INSERT INTO Product (ProductName, Description, Price, ImageFile, Buys, Available, UserId, Status, Rating, Author) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to add product, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getLong(1));
                }
            }

            // Thêm mối quan hệ vào ProductTag
            if (tagIds != null && !tagIds.isEmpty()) {
                String insertTagSql = "INSERT INTO ProductTag (ProductId, TagId) VALUES (?, ?)";
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
        String sql = "UPDATE Product SET ProductName = ?, Description = ?, Price = ?, ImageFile = ?, Buys = ?, Available = ?, " +
                "UserId = ?, Status = ?, Rating = ?, Author = ? WHERE ProductId = ?";
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
            statement.setLong(11, product.getProductId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update product, no rows affected.");
            }

            // Xóa các mối quan hệ cũ trong ProductTag
            String deleteTagSql = "DELETE FROM ProductTag WHERE ProductId = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteTagSql)) {
                deleteStatement.setLong(1, product.getProductId());
                deleteStatement.executeUpdate();
            }

            // Thêm các mối quan hệ mới
            if (tagIds != null && !tagIds.isEmpty()) {
                String insertTagSql = "INSERT INTO ProductTag (ProductId, TagId) VALUES (?, ?)";
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
        String sql = "UPDATE Product SET Status = 0 WHERE ProductId = ?";
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