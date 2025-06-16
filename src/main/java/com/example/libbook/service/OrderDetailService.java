package com.example.libbook.service;

import com.example.libbook.entity.OrderDetail;
import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);
}