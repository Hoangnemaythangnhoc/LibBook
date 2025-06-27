package com.example.libbook.service;

import com.example.libbook.dto.UserDTO;

import com.example.libbook.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    UserDTO checkLogin (String email,String pass);
    UserDTO getUserByEmail(String email);
    boolean uploadAvatar(byte[] base64, int ID) throws IOException;
    User getUserByUserId(int id);
    boolean updatePassword(String email, String password);
    List<UserDTO> getCustomers();
    List<UserDTO> getStaffWithRoleName();
    boolean banUser(int userId);
    boolean unbanUser(int userId);
    boolean createStaffAccount(UserDTO userDTO);

}