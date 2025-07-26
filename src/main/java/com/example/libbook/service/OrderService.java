package com.example.libbook.service;

import com.example.libbook.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
    void updateOrderStatus(Integer orderId, Integer newStatusId);
    boolean addOrder(Map<String, Object> orderData);
}