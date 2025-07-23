package com.example.libbook.service.impl;

import com.example.libbook.entity.Coupon;
import com.example.libbook.repository.CouponRepository;
import com.example.libbook.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public List<Coupon> getAllCoupons(String search) {
        return couponRepository.findAll(search);
    }

    @Override
    public void createCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    @Override
    public void updateCoupon(Integer id, Coupon coupon) {
        Coupon existing = couponRepository.findById(id);
        // Cập nhật các trường
        existing.setCode(coupon.getCode());
        existing.setDiscountPercent(coupon.getDiscountPercent());
        existing.setQuantity(coupon.getQuantity());
        existing.setStartDate(coupon.getStartDate());
        existing.setEndDate(coupon.getEndDate());
        existing.setIsActive(coupon.getIsActive());

        couponRepository.update(existing);
    }

    @Override
    public List<Coupon> getAllCouponsPaged(String search, int page, int size) {
        int offset = page * size;
        return couponRepository.findAllPaged(search, offset, size);
    }

    @Override
    public int countCoupons(String search) {
        return couponRepository.countAll(search);
    }

}