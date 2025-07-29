package com.example.libbook.utils;

import com.example.libbook.dto.OrderDataDTO;
import com.example.libbook.entity.Order;

import java.time.LocalDateTime;

public class Converter {

    AddressUtils addressUtils = new AddressUtils();
    public Order convertOrderDTOOrder (OrderDataDTO dto) {
        Order order = new Order();
        order.setCartItemDTOS(dto.getItems());
        String province = AddressUtils.loadProvinces(dto.getShipping().getProvince());
        String district = AddressUtils.getDistrictName(dto.getShipping().getProvince(), dto.getShipping().getDistrict());
        order.setAddress(dto.getShipping().getAddress() + ", " + district+ ", " +province);
        order.setCreateDate(LocalDateTime.now());
        order.setCouponId(dto.getPromoCode());
        order.setPaymentMethod(dto.getPayment());
        order.setPaymentStatus(0);
        order.setUserId(dto.getUserID());
        order.setTransCode(dto.getTransCode());
        return order;
    }
}

