package com.example.libbook.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Integer couponId;
    private String code;
    private BigDecimal discountPercent;
    private BigDecimal quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
