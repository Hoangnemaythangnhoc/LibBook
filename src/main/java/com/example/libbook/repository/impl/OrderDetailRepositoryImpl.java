package com.example.libbook.repository.impl;

import com.example.libbook.dto.CartItemDTO;
import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderDetail;
import com.example.libbook.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    private final DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public OrderDetailRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    @Override
    public boolean addNewOrderDetail(Order order, int OrderId) {
        String sql = "insert into [OrderDetail] values(?,?,?,?)";
        try (Connection connection = dataSource.getConnection()) {
            for (CartItemDTO dto : order.getCartItemDTOS()) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, OrderId);
                    pstmt.setLong(2, dto.getProductId());
                    if (order.getPaymentMethod().equals("bank_transfer")){
                        pstmt.setDouble(3, 0.0);  // Số lượng// Giả sử bạn có productId trong CartItemDTO
                    }
                    else {
                        pstmt.setDouble(3, dto.getPrice());
                    }// Số lượng
                    pstmt.setInt(4, dto.getQuantity());
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        System.err.println("Không thể chèn OrderDetail cho item: " + dto);
                        return false;
                    }
                }
            }
            return true; // Nếu tất cả các dòng đều chèn thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}