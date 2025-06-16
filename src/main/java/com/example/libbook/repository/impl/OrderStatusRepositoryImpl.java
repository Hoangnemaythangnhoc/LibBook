package com.example.libbook.repository.impl;

import com.example.libbook.entity.OrderStatus;
import com.example.libbook.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<OrderStatus> findAll() {
        return jdbcTemplate.query(
                "SELECT OrderStatusId, StatusName FROM OrderStatus",
                (rs, rowNum) -> {
                    OrderStatus status = new OrderStatus();
                    status.setOrderStatusId(rs.getInt("OrderStatusId"));
                    status.setStatusName(rs.getString("StatusName"));
                    return status;
                }
        );
    }
}