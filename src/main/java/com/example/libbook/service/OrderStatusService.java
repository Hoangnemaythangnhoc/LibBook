package com.example.libbook.service;

import com.example.libbook.entity.OrderStatus;
import java.util.List;

public interface OrderStatusService {
    List<OrderStatus> getAllOrderStatuses();
}