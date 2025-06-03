package com.example.libbook.service.impl;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.repository.UserRepository;
import com.example.libbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean createAccount(UserDTO userDTO) {
        // Kiểm tra email đã tồn tại
        if (isEmailExist(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        // Lưu tài khoản
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
}