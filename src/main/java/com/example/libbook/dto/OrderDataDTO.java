package com.example.libbook.dto;


import java.util.List;

public class OrderDataDTO {
    private List<CartItemDTO> items;
    private String payment;           // ví dụ: "cod", "bank"
    private int promoCode;         // có thể null
    private ShippingFormDTO shipping;
    private TotalDTO totals;
    private int UserID;
    private String transCode;

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(Integer userID) {
        UserID = userID;
    }

    public List<CartItemDTO> getItems() {
        return this.items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public int getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(int promoCode) {
        this.promoCode = promoCode;
    }

    public ShippingFormDTO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingFormDTO shipping) {
        this.shipping = shipping;
    }

    public TotalDTO getTotals() {
        return totals;
    }

    public void setTotals(TotalDTO totals) {
        this.totals = totals;
    }
}