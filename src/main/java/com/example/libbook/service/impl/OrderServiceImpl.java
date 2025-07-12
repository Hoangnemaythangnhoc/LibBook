
package com.example.libbook.service.impl;

import com.example.libbook.entity.Order;
import com.example.libbook.repository.OrderRepository;
import com.example.libbook.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        System.out.println("OrderService: Fetching all orders");
        List<Order> orders = orderRepository.getAllOrders();
        System.out.println("OrderService: Found " + orders.size() + " orders");
        return orders;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        System.out.println("OrderService: Fetching order with id: " + orderId);
        Order order = orderRepository.getOrderById(orderId);
        System.out.println("OrderService: Found order: " + (order != null ? order.getOrderId() : "null"));
        return order;
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer newStatusId) {
        System.out.println("OrderService: Updating status for order id: " + orderId + " to status: " + newStatusId);
        orderRepository.updateOrderStatus(orderId, newStatusId);
        System.out.println("OrderService: Updated status for order id: " + orderId);
    }
}
