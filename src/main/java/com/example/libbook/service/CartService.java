package com.example.libbook.service;

import com.example.libbook.dto.CartItemDTO;

import java.util.List;

public interface CartService {
    public void addOrUpdateCartItem(int userId, int bookId, int quantity);
    public int getCartItemCount(int userId);
    List<CartItemDTO> getCartItemsByUserId(int userId);
    void deleteAllCartItem(int userId);


}