package com.example.libbook.controller.Product;

import com.example.libbook.dto.PagedResponse;
import com.example.libbook.entity.Coupon;
import com.example.libbook.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public List<Coupon> getCoupons(@RequestParam(required = false, defaultValue = "") String search) {
        return couponService.getAllCoupons(search);
    }

    @PostMapping
    public void createCoupon(@RequestBody Coupon coupon) {
        couponService.createCoupon(coupon);
    }

    @PutMapping("/{id}")
    public void updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        couponService.updateCoupon(id, coupon);
    }

    @GetMapping("/paged")
    public PagedResponse<Coupon> getCouponsPaged(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        int total = couponService.countCoupons(search);
        List<Coupon> data = couponService.getAllCouponsPaged(search, page, size);
        int totalPages = (int) Math.ceil((double) total / size);

        return new PagedResponse<>(data, page, totalPages);
    }

}