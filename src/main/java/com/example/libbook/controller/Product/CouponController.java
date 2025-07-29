package com.example.libbook.controller.Product;

import com.example.libbook.dto.PagedResponse;
import com.example.libbook.entity.Coupon;
import com.example.libbook.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/coupon-check-code")
    public ResponseEntity<Integer> checkCouponCode(@RequestParam Map<String, Object> promoCode) {
        // Extract the coupon code from the map
        String code = promoCode.get("code") != null ? promoCode.get("code").toString() : null;

        // Validate input
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(0); // Return 400 Bad Request if code is missing or empty
        }

        try {
            // Call the service method to check the coupon code
            int discountPercent = couponService.checkCouponCode(code);
            return ResponseEntity.ok(discountPercent); // Return 200 OK with the discount percentage
        } catch (Exception e) {
            // Handle any database or unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }
}
