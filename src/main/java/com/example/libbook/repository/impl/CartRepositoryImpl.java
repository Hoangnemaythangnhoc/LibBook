package com.example.libbook.repository.impl;

import com.example.libbook.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepositoryImpl implements CartRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void addToCart(int userId, int bookId, int quantity) {
        String sql = "INSERT INTO CartItem VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, userId, bookId, quantity);
    }
}