package com.example.libbook.repository;

import com.example.libbook.dto.CartItemDTO;

import java.util.List;

public interface CartRepository {

    public void addOrUpdateCartItem(int userId, int bookId, int quantity);
    public int getCartItemCount(int userId);
    List<CartItemDTO> getCartItemsByUserId(int userId);
    void deleteAllCartItem(int userId);
  
    public void addToCart(int userId, int bookId, int quantity);
}