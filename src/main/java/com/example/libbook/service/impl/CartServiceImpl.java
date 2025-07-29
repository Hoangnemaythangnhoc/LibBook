package com.example.libbook.service.impl;


import com.example.libbook.dto.CartItemDTO;
import com.example.libbook.repository.CartRepository;
import com.example.libbook.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Override
    public void addOrUpdateCartItem(int userId, int bookId, int quantity) {
        cartRepository.addOrUpdateCartItem(userId, bookId, quantity);
    }

    @Override
    public int getCartItemCount(int userId) {
        return cartRepository.getCartItemCount(userId);
    }

    @Override
    public List<CartItemDTO> getCartItemsByUserId(int userId) {
        return cartRepository.getCartItemsByUserId(userId);
    }

    @Override
    public void deleteAllCartItem(int userId) {
        cartRepository.deleteAllCartItem(userId);
    }
}
