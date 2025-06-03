package com.example.libbook.repository.impl;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.repository.UserRepository;
import com.example.libbook.utils.ConnectUtils;
import org.springframework.stereotype.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@Repository
public class UserRepositoryImpl implements UserRepository {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @Override
    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE Email = ?";
        ConnectUtils db = ConnectUtils.getInstance();
        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertAccount(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String sql = "INSERT INTO [User] (UserName, RoleId, Email, Password, Status) VALUES (?, ?, ?, ?, ?)";
        ConnectUtils db = ConnectUtils.getInstance();
        String userName = userDTO.getEmail().split("@")[0];
        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userName);
            statement.setInt(2, 2);
            statement.setString(3, userDTO.getEmail());
            statement.setString(4, hashPassword(userDTO.getPassword()));
            statement.setBoolean(5, userDTO.isStatus());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createAccount(UserDTO userDTO) {
        try {
            insertAccount(userDTO);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserDTO checkLogin(String email, String pass) {
        String sql = "Select Top 1 *  from [User] where Email = ?";
        ConnectUtils db = ConnectUtils.getInstance();
        UserDTO userDTO = null ;
        String password = null;
        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userDTO = new UserDTO();
                userDTO.setEmail(resultSet.getString("Email"));
                 userDTO.setUserName(resultSet.getString("UserName"));
                 userDTO.setUserId(resultSet.getInt("UserId"));
                 userDTO.setRoleID(resultSet.getInt("RoleId"));
                 password = resultSet.getString("Password");
            }
            if (password.equals(hashPassword(pass)))
                return userDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        String sql = "Select Top 1 *  from [User] where Email = ?";
        ConnectUtils db = ConnectUtils.getInstance();
        UserDTO userDTO = null ;
        try (Connection connection = db.openConection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userDTO = new UserDTO();
                userDTO.setEmail(resultSet.getString("Email"));
                userDTO.setUserName(resultSet.getString("UserName"));
                userDTO.setUserId(resultSet.getInt("UserId"));
                userDTO.setRoleID(resultSet.getInt("RoleId"));
            }
            return userDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}