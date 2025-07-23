package com.example.libbook.service;

import com.example.libbook.entity.Coupon;

import java.util.List;

public interface CouponService {
    List<Coupon> getAllCoupons(String search);
    void createCoupon(Coupon coupon);
    void updateCoupon(Integer id, Coupon coupon);
    List<Coupon> getAllCouponsPaged(String search, int page, int size);
    int countCoupons(String search);

}