package com.example.libbook.repository;

import com.example.libbook.entity.CheckBuy;

public interface CheckBuyRepository {
    boolean existsByUserIdAndProductId(int userId, int productId); // Bỏ tham số status
    boolean saveCheckBuy(CheckBuy checkBuy);
}