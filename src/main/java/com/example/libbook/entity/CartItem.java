package com.example.libbook.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
@lombok.Setter
@lombok.Getter
public class CartItem {

    private int CartItemId;
    private int CartId;
    private int ProductId;
    private int Quantity;

    public CartItem(int cartItemId, int cartId, int productId, int quantity) {
        CartItemId = cartItemId;
        CartId = cartId;
        ProductId = productId;
        Quantity = quantity;
    }
    public CartItem() {
        CartItemId = 0;
        CartId = 0;
        ProductId = 0;
        Quantity = 0;
    }

}
