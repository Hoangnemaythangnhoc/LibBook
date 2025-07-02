package com.example.libbook.service.impl;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.repository.UserRepository;
import com.example.libbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean createAccount(UserDTO userDTO) {
        if (isEmailExist(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        return userRepository.createAccount(userDTO);
    }

    @Override
    public UserDTO checkLogin(String email, String pass) {
        return userRepository.checkLogin(email, pass);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.isEmailExist(email);
    }

    @Override
    public boolean uploadAvatar(byte[] base64,int ID) throws IOException {
        return userRepository.updateAvatar(base64, 1,ID);
    }

    @Override
    public User getUserByUserId(int id) {
        return userRepository.getUserByUserId(id);
    }

    @Override
    public boolean updatePassword(String email, String password) {
        return userRepository.updatePassword(email,password);
    }

    @Override
    public List<UserDTO> getCustomers() {
        return userRepository.getCustomers();
    }

    @Override
    public List<UserDTO> getStaffWithRoleName() {
        return userRepository.getStaffWithRoleName();
    }

    @Override
    public boolean banUser(int userId) {
        return userRepository.banUser(userId);
    }

    @Override
    public boolean unbanUser(int userId) {
        return userRepository.unbanUser(userId);
    }

    @Override
    public boolean createStaffAccount(UserDTO userDTO) {
        if (userRepository.isEmailExist(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        return userRepository.createStaffAccount(userDTO);
    }

}