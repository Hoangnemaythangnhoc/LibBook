package com.example.libbook.repository.impl;

import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderDetail;
import com.example.libbook.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
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

    public int addNewOrder(Order order) {
        int generatedId = 0;
        String sql = "INSERT INTO [Order](UserId, CreateDate, OrderStatusId, Address, Paymentstatus, PaymentID) VALUES (?,?,?,?,?,?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getUserId());
            pstmt.setDate(2, java.sql.Date.valueOf(order.getCreateDate().toLocalDate()));

            pstmt.setString(4, order.getAddress());

            if ("bank_transfer".equals(order.getPaymentMethod())) {
                if (order.getTransCode() == null || order.getTransCode().isEmpty()) {
                    throw new IllegalArgumentException("transCode không được null hoặc rỗng cho thanh toán chuyển khoản");
                }
                pstmt.setInt(3, 2); // OrderStatusId mặc định là 1
                pstmt.setInt(5, 1); // Paymentstatus = 1 (đã thanh toán)
                pstmt.setString(6, order.getTransCode());
            } else {
                pstmt.setInt(3, 1); // OrderStatusId mặc định là 1
                pstmt.setInt(5, order.getPaymentStatus()); // Paymentstatus từ Order
                pstmt.setNull(6, java.sql.Types.VARCHAR); // PaymentID = NULL cho cod
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi chèn đơn hàng: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e; // Ném lại để controller xử lý
        }

        return generatedId;
    }

    @Override
    public boolean cancelOrder(Order order) {
        String sql = "Update [Order] set OrderStatusId = ? where OrderId ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 4);
            pstmt.setInt(2, order.getOrderId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("OrderRepository: Error updating order status for id " + order.getOrderId() + ": " + e.getMessage());
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