package com.example.libbook.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetail {
    private Integer orderDetailId;
    private Integer orderId;
    private Integer productId;
    private Double price;
    private Integer quantity;
}