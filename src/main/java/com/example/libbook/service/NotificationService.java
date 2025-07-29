package com.example.libbook.service;

import com.example.libbook.entity.Coupon;
import com.example.libbook.entity.Product;

public interface NotificationService {
    void sendNewProductNotification(Product product);
    void sendNewVoucherNotification(Coupon coupon);
}
