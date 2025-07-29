package com.example.libbook.entity;

import java.util.Date;

public class Payment {
    private String paymentID;     // PaymentID VARCHAR PRIMARY KEY
    private Date createAt;        // CreateAt DATETIME
    private String amount;        // Amount VARCHAR
    private boolean status;       // status BIT
    private int orderId;          // orderId INT (foreign key)

    // Constructor mặc định
    public Payment() {}

    // Constructor đầy đủ
    public Payment(String paymentID, Date createAt, String amount, boolean status, int orderId) {
        this.paymentID = paymentID;
        this.createAt = createAt;
        this.amount = amount;
        this.status = status;
        this.orderId = orderId;
    }

    // Getter và Setter
    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}

