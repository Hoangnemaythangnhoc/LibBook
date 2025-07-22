package com.example.libbook.repository.impl;

import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderDetail;
import com.example.libbook.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final DataSource dataSource;

    @Autowired
    public OrderRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("OrderRepositoryImpl: Initialized with DataSource: " + (dataSource != null ? "Yes" : "No"));
    }

    @Override
    public List<Order> getAllOrders() {
        System.out.println("OrderRepository: Fetching all orders");
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM [Order]";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderDetails(getOrderDetailsByOrderId(order.getOrderId(), connection));
                orders.add(order);
            }
            System.out.println("OrderRepository: Found " + orders.size() + " orders");
        } catch (Exception e) {
            System.err.println("OrderRepository: Error fetching all orders: " + e.getMessage());
            throw new RuntimeException("Error fetching all orders", e);
        }
        return orders;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        System.out.println("OrderRepository: Fetching order with id: " + orderId);
        String sql = "SELECT * FROM [Order] WHERE OrderId = ?";
        Order order = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = mapResultSetToOrder(rs);
                    order.setOrderDetails(getOrderDetailsByOrderId(orderId, connection));
                }
            }
            System.out.println("OrderRepository: Found order: " + (order != null ? order.getOrderId() : "null"));
        } catch (Exception e) {
            System.err.println("OrderRepository: Error fetching order with id " + orderId + ": " + e.getMessage());
            throw new RuntimeException("Error fetching order by id", e);
        }
        return order;
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer newStatusId) {
        System.out.println("OrderRepository: Updating status for order id: " + orderId + " to status: " + newStatusId);
        String sql = "UPDATE [Order] SET OrderStatusId = ? WHERE OrderId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newStatusId);
            pstmt.setInt(2, orderId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("OrderRepository: Updated " + rowsAffected + " rows for order id: " + orderId);
            if (rowsAffected == 0) {
                throw new RuntimeException("No order found with id: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("OrderRepository: Error updating order status for id " + orderId + ": " + e.getMessage());
            throw new RuntimeException("Error updating order status", e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws Exception {
        Order order = new Order();
        order.setOrderId(rs.getInt("OrderId"));
        order.setUserId(rs.getInt("UserId"));
        Timestamp timestamp = rs.getTimestamp("CreateDate");
        order.setCreateDate(timestamp != null ? timestamp.toLocalDateTime() : null);
        order.setComplete(rs.getInt("Complete"));
        order.setCouponId(rs.getInt("CouponId") == 0 ? null : rs.getInt("CouponId"));
        order.setOrderStatusId(rs.getInt("OrderStatusId"));
        order.setAddress(rs.getString("Address"));
        return order;
    }

    private List<OrderDetail> getOrderDetailsByOrderId(Integer orderId, Connection connection) {
        System.out.println("OrderRepository: Fetching order details for order id: " + orderId);
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM [OrderDetail] WHERE OrderId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("OrderDetailId"));
                    detail.setOrderId(rs.getInt("OrderId"));
                    detail.setProductId(rs.getInt("ProductId"));
                    detail.setPrice(rs.getDouble("Price"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    details.add(detail);
                }
            }
            System.out.println("OrderRepository: Found " + details.size() + " order details for order id: " + orderId);
        } catch (Exception e) {
            System.err.println("OrderRepository: Error fetching order details for id " + orderId + ": " + e.getMessage());
        }
        return details;
    }
}