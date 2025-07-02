package com.example.libbook.service;

import com.example.libbook.entity.CheckBuy;

public interface CheckBuyService {
    boolean hasUserReviewed(int userId, int productId);
    boolean saveCheckBuy(CheckBuy checkBuy);
}