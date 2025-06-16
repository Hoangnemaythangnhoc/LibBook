package com.example.libbook.service;

import com.example.libbook.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
    void updateOrderStatus(Integer orderId, Integer newStatusId);
}