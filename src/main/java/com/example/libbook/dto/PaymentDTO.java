package com.example.libbook.dto;

import com.example.libbook.entity.CartItem;

import java.sql.Timestamp;
import java.util.List;

public class PaymentDTO {
    private double amount;
    private String transCode;
    private int userId;
    private List<CartItem> cartItem;
    private double cartBill;
    private Timestamp creatAt;

    // Constructors
    public PaymentDTO() {}

    public PaymentDTO(double amount, String transCode, int userId, List<CartItem> cartItem, double cartBill) {
        this.amount = amount;
        this.transCode = transCode;
        this.userId = userId;
        this.cartItem = cartItem;
        this.cartBill = cartBill;
    }

    // Getters and Setters


    public Timestamp getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(Timestamp creatAt) {
        this.creatAt = creatAt;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartItem() {
        return cartItem;
    }

    public void setCartItem(List<CartItem> cartItem) {
        this.cartItem = cartItem;
    }

    public double getCartBill() {
        return cartBill;
    }

    public void setCartBill(double cartBill) {
        this.cartBill = cartBill;
    }
}

