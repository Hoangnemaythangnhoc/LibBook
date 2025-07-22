package com.example.libbook.dto;

import com.example.libbook.entity.Product;

import java.sql.Timestamp;

public class CartItemDTO extends Product {
    private int quantity;

    public CartItemDTO() {
        super();
        this.quantity = 0;
    }

    public CartItemDTO(Long productId, String productName, String description,
                       int buys, int available, double price, String imageFile,
                       Long userId, int status, double rating, String author, Timestamp createAt, int discount, String publisher, int quantity) {
        super(productId, productName, description, buys, available, price, imageFile, userId, status, rating, author, createAt, discount, publisher);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}