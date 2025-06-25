package com.example.libbook.repository;

import com.example.libbook.entity.OrderStatus;
import java.util.List;

public interface OrderStatusRepository {
    List<OrderStatus> findAll();
}