package com.example.libbook.repository;

import com.example.libbook.dto.UserDTO;

public interface UserRepository {
    boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin(String email, String pass);
    UserDTO getUserByEmail(String email);
}
