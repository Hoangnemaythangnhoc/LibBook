package com.example.libbook.repository.impl;

import com.example.libbook.entity.OrderDetail;
import com.example.libbook.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        return jdbcTemplate.query(
                "SELECT OrderDetailId, OrderId, ProductId, Price, Quantity FROM OrderDetail WHERE OrderId = ?",
                new Object[]{orderId},
                (rs, rowNum) -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("OrderDetailId"));
                    detail.setOrderId(rs.getInt("OrderId"));
                    detail.setProductId(rs.getInt("ProductId"));
                    detail.setPrice(rs.getDouble("Price"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    return detail;
                }
        );
    }
}