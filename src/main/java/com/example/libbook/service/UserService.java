package com.example.libbook.service;

import com.example.libbook.dto.UserDTO;

public interface UserService {
    public boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin (String email,String pass);
    UserDTO getUserByEmail(String email);

}
