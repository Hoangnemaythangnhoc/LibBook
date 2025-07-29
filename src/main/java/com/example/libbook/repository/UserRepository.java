package com.example.libbook.repository;

import com.example.libbook.dto.UserDTO;

import com.example.libbook.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    boolean isEmailExist(String email);
    boolean createAccount(UserDTO userDTO);
    User checkLogin(String email, String pass);
    User getUserByEmail(String email);
    boolean updateAvatar(byte[] base64, int type, int ID) throws IOException;
    User getUserByUserId(int id);
    boolean updatePassword(String email,String password);
    List<UserDTO> getCustomers();
    List<UserDTO> getStaffWithRoleName();
    boolean banUser(int userId);
    boolean unbanUser(int userId);
    boolean createStaffAccount(UserDTO userDTO);
    boolean updateStaffRole(int userId, int roleId);
    void updateUser(User user);
    void updateUserSubscription(int userId, boolean isSubscribed);
    List<String> getSubscribedEmails();
    boolean checkBanAccount(String email);
}