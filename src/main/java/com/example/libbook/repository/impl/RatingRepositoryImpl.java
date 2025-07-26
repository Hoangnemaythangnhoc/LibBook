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
    public List<Rating> getAllRatings() {
        String sql = "SELECT * FROM [Rating]";
        List<Rating> ratings = new ArrayList<>();
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
                rating.setStatus(rs.getBoolean("Status"));
                ratings.add(rating);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tất cả rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public List<Rating> getRatingsByProductId(int productId) {
        String sql = "SELECT * FROM [Rating] WHERE [ProductId] = ? AND Status = 1";
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
                rating.setStatus(rs.getBoolean("Status"));
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
        String ratingSql = "INSERT INTO [Rating] (UserId, ProductId, Stars, Content, CreatedDate, Status) VALUES (?, ?, ?, ?, ?, ?)";
        String updateRatingSql = "UPDATE [Product] SET [Rating] = (SELECT AVG(CAST(Stars AS DECIMAL(18, 2))) FROM [Rating] WHERE ProductId = ?) WHERE ProductId = ?";
        ConnectUtils db = ConnectUtils.getInstance();

        Connection conn = null;
        PreparedStatement ratingStmt = null;
        PreparedStatement updateStmt = null;

        try {
            conn = db.openConnection();
            conn.setAutoCommit(false);

            ratingStmt = conn.prepareStatement(ratingSql);
            ratingStmt.setInt(1, rating.getUserId());
            ratingStmt.setInt(2, rating.getProductId());
            ratingStmt.setInt(3, rating.getStars());
            ratingStmt.setString(4, rating.getContent());
            ratingStmt.setObject(5, rating.getCreatedAt());
            ratingStmt.setBoolean(6, true);
            int ratingRowsAffected = ratingStmt.executeUpdate();

            if (ratingRowsAffected > 0) {
                updateStmt = conn.prepareStatement(updateRatingSql);
                updateStmt.setInt(1, rating.getProductId());
                updateStmt.setInt(2, rating.getProductId());
                int updateRowsAffected = updateStmt.executeUpdate();

                if (updateRowsAffected >= 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Lỗi khi rollback giao dịch", rollbackEx);
            }
            throw new RuntimeException("Lỗi khi lưu rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ratingStmt != null) ratingStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi đóng tài nguyên", e);
            }
        }
    }

    @Override
    public boolean updateRatingStatus(int ratingId, boolean status) {
        String sql = "UPDATE [Rating] SET Status = ? WHERE RatingId = ?";
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, ratingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái rating", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}