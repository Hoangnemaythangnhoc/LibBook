package com.example.libbook.service.impl;

import com.example.libbook.entity.CheckBuy;
import com.example.libbook.repository.CheckBuyRepository;
import com.example.libbook.service.CheckBuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckBuyServiceImpl implements CheckBuyService {

    @Autowired
    private CheckBuyRepository checkBuyRepository;

    @Override
    public boolean hasUserReviewed(int userId, int productId) {
        return checkBuyRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean saveCheckBuy(CheckBuy checkBuy) {
        return checkBuyRepository.saveCheckBuy(checkBuy);
    }
}
