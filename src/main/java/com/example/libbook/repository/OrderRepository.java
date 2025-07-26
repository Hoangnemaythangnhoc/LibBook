package com.example.libbook.repository;

import com.example.libbook.dto.OrderDataDTO;
import com.example.libbook.entity.Order;

import java.util.List;

public interface OrderRepository {
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
    void updateOrderStatus(Integer orderId, Integer newStatusId);
    int addNewOrder(Order order);
    boolean cancelOrder(Order order);
}