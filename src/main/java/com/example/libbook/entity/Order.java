package com.example.libbook.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Order {
    private Integer orderId;
    private Integer userId;
    private LocalDateTime createDate;
    private Integer complete;
    private Integer couponId;
    private Integer orderStatusId;
    private List<OrderDetail> orderDetails;
    private OrderStatus orderStatus;
}