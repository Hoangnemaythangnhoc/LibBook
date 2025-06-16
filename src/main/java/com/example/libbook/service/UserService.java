package com.example.libbook.service;

import com.example.libbook.dto.UserDTO;

import com.example.libbook.entity.User;

import java.io.IOException;

public interface UserService {
    public boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin (String email,String pass);
    UserDTO getUserByEmail(String email);
    boolean uploadAvatar(String base64, int ID) throws IOException;
    User getUserByUserId(int id);

}
