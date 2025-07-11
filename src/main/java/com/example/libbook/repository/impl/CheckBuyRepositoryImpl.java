package com.example.libbook.repository.impl;

import com.example.libbook.entity.CheckBuy;
import com.example.libbook.repository.CheckBuyRepository;
import com.example.libbook.utils.ConnectUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CheckBuyRepositoryImpl implements CheckBuyRepository {

    @Override
    public boolean existsByUserIdAndProductId(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM [CheckBuy] WHERE [UserId] = ? AND [ProductId] = ?";
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of CheckBuy: UserId=" + userId + ", ProductId=" + productId, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean saveCheckBuy(CheckBuy checkBuy) {
        ConnectUtils db = ConnectUtils.getInstance();
        String checkSql = "SELECT Status FROM [CheckBuy] WHERE [UserId] = ? AND [ProductId] = ?";

        try (Connection conn = db.openConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, checkBuy.getUserId());
            checkStmt.setInt(2, checkBuy.getProductId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean currentStatus = rs.getBoolean("Status");
                if (currentStatus) {
                    return false;
                } else {
                    String updateSql = "UPDATE [CheckBuy] SET [Status] = ? WHERE [UserId] = ? AND [ProductId] = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setBoolean(1, true);
                        updateStmt.setInt(2, checkBuy.getUserId());
                        updateStmt.setInt(3, checkBuy.getProductId());
                        int rowsAffected = updateStmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            } else {
                throw new IllegalStateException("You have not purchased this product, so you cannot rate it.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking or updating CheckBuy: UserId=" + checkBuy.getUserId() + ", ProductId=" + checkBuy.getProductId(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

