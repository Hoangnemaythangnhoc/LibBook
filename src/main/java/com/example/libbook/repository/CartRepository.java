package com.example.libbook.repository;

public interface CartRepository {
    public void addToCart(int userId, int bookId, int quantity);
}   