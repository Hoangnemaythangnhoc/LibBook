package com.example.libbook.repository.impl;

import com.example.libbook.dto.CartItemDTO;
import com.example.libbook.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CartRepositoryImpl implements CartRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void addOrUpdateCartItem(int userId, int bookId, int quantity) {
        String sql = "EXEC AddOrUpdateCartItem @UserId = ?, @ProductId = ?, @Quantity = ?;";
        jdbcTemplate.update(sql, userId, bookId, quantity);
    }

    @Override
    public int getCartItemCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Cart c join CartItem ct on c.CartId = ct.CartId WHERE c.UserId = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public List<CartItemDTO> getCartItemsByUserId(int userId) {
        String sql = "SELECT * " +
                "FROM dbo.product p " +
                "JOIN CartItem c ON c.ProductId = p.ProductId " +
                "JOIN Cart ct ON ct.CartId = c.CartId " +
                "WHERE ct.UserId = ?";

        return jdbcTemplate.query(sql, new Object[]{userId}, new RowMapper<CartItemDTO>() {
            @Override
            public CartItemDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                CartItemDTO item = new CartItemDTO();
                item.setProductId(rs.getLong("ProductId"));
                item.setProductName(rs.getString("ProductName"));
                item.setPrice(rs.getDouble("Price"));
                item.setImageFile(rs.getString("ImageFile"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setUserId(rs.getLong("UserId"));
                item.setAuthor(rs.getString("Author"));
                item.setRating(rs.getInt("Rating"));
                item.setAvailable(rs.getInt("Available"));
                return item;
            }
        });
    }

    @Override
    public void deleteAllCartItem(int userId) {
        String sql = " DELETE CI FROM CartItem CI INNER JOIN Cart C ON CI.CartId = C.CartId WHERE C.UserId = ? ";
        jdbcTemplate.update(sql, userId);
    }



}
