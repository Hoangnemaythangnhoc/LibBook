package com.example.libbook.service;

import com.example.libbook.entity.Product;

public interface NotificationService {
    void sendNewProductNotification(Product product);
}