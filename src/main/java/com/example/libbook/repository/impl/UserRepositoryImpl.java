package com.example.libbook.repository.impl;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.repository.UserRepository;
import com.example.libbook.utils.ConnectUtils;
import com.example.libbook.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private final ImageUtils imageUtils;

    JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }

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
        UserDTO userDTO = null;
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
        UserDTO userDTO = null;
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

    @Override
    public boolean updateAvatar(byte[] base64, int type, int ID) throws IOException {
        String pathImage = imageUtils.uploadAvatar(base64, type);
        ConnectUtils db = ConnectUtils.getInstance();
        if (pathImage != null && !pathImage.isEmpty()) {
            String sql = "UPDATE [User] SET [ProfilePicture] = ? WHERE [UserId] = ?";
            try (Connection connection = db.openConection();
                    PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, pathImage);
                statement.setInt(2, ID);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public User getUserByUserId(int id) {
        String sql = "SELECT * FROM [User] WHERE [UserId] = ?";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[] { id },
                new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public boolean updatePassword(String email, String password) {
        String hashPass = hashPassword(password);
        ConnectUtils db = ConnectUtils.getInstance();
        String sql = "UPDATE [User] SET [Password] = ? WHERE [Email] = ? ";
        try (Connection connection = db.openConection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hashPass);
            statement.setString(2, email);
            int check = statement.executeUpdate();
            return check > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}