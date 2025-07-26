package com.example.libbook.repository;

import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderDetail;
import java.util.List;

public interface OrderDetailRepository {
    List<OrderDetail> findByOrderId(Integer orderId);
    boolean addNewOrderDetail(Order order, int OrderId);
}