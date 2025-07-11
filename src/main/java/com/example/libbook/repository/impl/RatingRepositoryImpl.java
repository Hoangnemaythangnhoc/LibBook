package com.example.libbook.repository.impl;

import com.example.libbook.entity.Rating;
import com.example.libbook.repository.RatingRepository;
import com.example.libbook.utils.ConnectUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RatingRepositoryImpl implements RatingRepository {
    @Override
    public List<Rating> getRatingsByProductId(int productId) {
        String sql = "SELECT * FROM [Rating] WHERE [ProductId] = ?";
        List<Rating> ratings = new ArrayList<>();
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Rating rating = new Rating();
                rating.setRatingId(rs.getInt("RatingId"));
                rating.setProductId(rs.getInt("ProductId"));
                rating.setUserId(rs.getInt("UserId"));
                rating.setStars(rs.getInt("Stars"));
                rating.setCreatedAt(rs.getTimestamp("CreatedDate") != null ?
                        rs.getTimestamp("CreatedDate").toLocalDateTime() : null);
                rating.setContent(rs.getString("Content"));
                ratings.add(rating);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách rating với ProductId: " + productId, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public boolean saveRating(Rating rating) {
        String ratingSql = "INSERT INTO [Rating] (UserId, ProductId, Stars, Content, CreatedDate) VALUES (?, ?, ?, ?, ?)";
        String checkBuySql = "INSERT INTO [CheckBuy] (UserId, ProductId, Status) VALUES (?, ?, ?)";
        ConnectUtils db = ConnectUtils.getInstance();

        Connection connection = null;
        PreparedStatement ratingStmt = null;
        PreparedStatement checkBuyStmt = null;

        try {
            connection = db.openConnection();
            connection.setAutoCommit(false); // Start transaction

            ratingStmt = connection.prepareStatement(ratingSql);
            ratingStmt.setInt(1, rating.getUserId());
            ratingStmt.setInt(2, rating.getProductId());
            ratingStmt.setInt(3, rating.getStars());
            ratingStmt.setString(4, rating.getContent());
            ratingStmt.setObject(5, rating.getCreatedAt());
            int ratingRowsAffected = ratingStmt.executeUpdate();

            checkBuyStmt = connection.prepareStatement(checkBuySql);
            checkBuyStmt.setInt(1, rating.getUserId());
            checkBuyStmt.setInt(2, rating.getProductId());
            checkBuyStmt.setInt(3, 1); // Status = 1
            int checkBuyRowsAffected = checkBuyStmt.executeUpdate();

            if (ratingRowsAffected > 0 && checkBuyRowsAffected > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Lỗi khi rollback giao dịch", rollbackEx);
            }
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Lỗi khi rollback giao dịch", rollbackEx);
            }
            throw new RuntimeException("Lỗi khi lưu rating và checkbuy", e);
        } finally {
            try {
                if (ratingStmt != null) ratingStmt.close();
                if (checkBuyStmt != null) checkBuyStmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi đóng tài nguyên", e);
            }
        }
    }
}
