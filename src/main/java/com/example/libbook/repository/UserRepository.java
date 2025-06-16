package com.example.libbook.repository;

import com.example.libbook.dto.UserDTO;

import com.example.libbook.entity.User;

import java.io.IOException;

public interface UserRepository {
    boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin(String email, String pass);
    UserDTO getUserByEmail(String email);
    boolean updateAvatar(String base64, int type, int ID) throws IOException;
    User getUserByUserId(int id);
    boolean changePassword(User user);

}