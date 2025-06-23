package com.example.libbook.repository;

import com.example.libbook.dto.UserDTO;

import com.example.libbook.entity.User;

import java.io.IOException;

public interface UserRepository {
    boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin(String email, String pass);
    UserDTO getUserByEmail(String email);
    boolean updateAvatar(byte[] base64, int type, int ID) throws IOException;
    User getUserByUserId(int id);
    boolean updatePassword(String email,String password);

}