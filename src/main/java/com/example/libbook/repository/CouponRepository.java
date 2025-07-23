package com.example.libbook.repository;

import com.example.libbook.entity.Coupon;

import java.util.List;

public interface CouponRepository {
    List<Coupon> findAll(String search);
    void save(Coupon coupon);
    void update(Coupon coupon);
    Coupon findById(Integer id);
    List<Coupon> findAllPaged(String search, int offset, int size);
    int countAll(String search);

}