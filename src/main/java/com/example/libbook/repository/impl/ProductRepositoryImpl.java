package com.example.libbook.repository.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.entity.Tag;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.service.NotificationService;
import com.example.libbook.service.impl.NotificationServiceImpl;
import com.example.libbook.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
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
    private final ImageUtils imageUtils;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public ProductRepositoryImpl(DataSource dataSource, ImageUtils imageUtils, ImageUtils imageUtils1) {
        this.dataSource = dataSource;
        this.imageUtils = imageUtils1;
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
        product.setAuthor(rs.getString("author"));
        product.setDiscount(rs.getInt("discount"));
        product.setPublisher(rs.getString("publisher"));
        return product;
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
                product.setCreateAt(resultSet.getTimestamp("createAt"));
                product.setDiscount(resultSet.getInt("discount"));
                product.setPublisher(resultSet.getString("publisher"));
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
                    product.setPublisher(resultSet.getString("publisher"));
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
                    product.setPublisher(resultSet.getString("publisher"));
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
    public Long addProduct(Product product, List<Long> tagIds) throws IOException {
        byte[] baseImage = imageUtils.decodeBase64(product.getImageFile());
        String image = imageUtils.uploadAvatar(baseImage, 2);
        product.setImageFile(image);
        notificationService.sendNewProductNotification(product);
        String sql = "INSERT INTO product (productName, description, price, imageFile, buys, available, userId, status, rating, author, discount, publisher) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            statement.setString(12, product.getPublisher());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to add product, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                     product.setProductId(generatedKeys.getLong(1));
                     return generatedKeys.getLong(1);

                }
            }

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
        return null;
    }

    @Override
    public void updateProduct(Product product, List<Long> tagIds) throws IOException, SQLException {
        int affectedRows = 0;
        try (Connection connection = dataSource.getConnection()) {
            String image = null;
            boolean hasNewImage = true;
            if (product.getImageFile() == null || product.getImageFile().isEmpty()) {
                hasNewImage = false;
            }

            if (hasNewImage) {
                byte[] baseImage = imageUtils.decodeBase64(product.getImageFile());
                image = imageUtils.uploadAvatar(baseImage, 2);
            }

            String sql = hasNewImage
                    ? "UPDATE product SET productName = ?, description = ?, price = ?, imageFile = ?, buys = ?, available = ?, " +
                    "userId = ?, status = ?, rating = ?, author = ?, discount = ?, publisher = ? WHERE productId = ?"
                    : "UPDATE product SET productName = ?, description = ?, price = ?, buys = ?, available = ?, " +
                    "userId = ?, status = ?, rating = ?, author = ?, discount = ?, publisher = ? WHERE productId = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getProductName());
                statement.setString(2, product.getDescription());
                statement.setDouble(3, product.getPrice());

                int index = 4;
                if (hasNewImage) {
                    statement.setString(index++, image);
                }
                statement.setInt(index++, product.getBuys());
                statement.setInt(index++, product.getAvailable());
                statement.setLong(index++, product.getUserId());
                statement.setInt(index++, product.getStatus());
                statement.setDouble(index++, product.getRating());
                statement.setString(index++, product.getAuthor());
                statement.setInt(index++, product.getDiscount());
                statement.setString(index++, product.getPublisher());
                statement.setLong(index, product.getProductId());

                affectedRows = statement.executeUpdate();
            }

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update product, no rows affected.");
            }

            String deleteTagSql = "DELETE FROM ProductTag WHERE ProductId = ?";
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
                throw new RuntimeException("Failed to delete product, no rows affected.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            throw new RuntimeException("Error deleting product", e);
        }
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

    @Override
    public List<String> getRandomTags(int limit) {
        return List.of();
    }

    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT TagName FROM Tag";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tags.add(rs.getString("TagName"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tags", e);
        }
        return tags;
    }

    @Override
    public Long addProductFromCSV(Product product, List<Long> tagIds) {
        String sql = "INSERT INTO product (productName, description, price, imageFile, buys, available, userId, status, rating, author, discount, publisher) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            statement.setInt(11, product.getDiscount());
            statement.setString(12, product.getPublisher());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to add product, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getLong(1));
                    return generatedKeys.getLong(1);

                }
            }

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
        return null;
    }

    @Override
    public void updateQuantityProduct(int productID, int quantity) {
        int quantityProduct = getProductById((long) productID).getAvailable();
        String sql = "update product set [Available] = ? where [ProductId] = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantityProduct - quantity);
            statement.setInt(2, productID);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to delete product, no rows affected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Product> getAllDataChart() {
        return List.of();
    }
}