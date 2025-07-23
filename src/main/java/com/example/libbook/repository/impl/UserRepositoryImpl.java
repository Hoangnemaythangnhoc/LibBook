package com.example.libbook.repository.impl;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.repository.UserRepository;
import com.example.libbook.utils.ConnectUtils;
import com.example.libbook.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private final ImageUtils imageUtils;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

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
        try (Connection connection = db.openConnection();
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
        try (Connection connection = db.openConnection();
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

    public User checkLogin(String email, String pass) {
        String sql = "Select Top 1 *  from [User] where Email = ?";
        ConnectUtils db = ConnectUtils.getInstance();
        User userDTO = null ;
        String password = null;
        try (Connection connection = db.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userDTO = new User();
                userDTO.setEmail(resultSet.getString("Email"));
                userDTO.setUserName(resultSet.getString("UserName"));
                userDTO.setUserId(resultSet.getInt("UserId"));
                userDTO.setRoleId(resultSet.getInt("RoleId"));
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
    public User getUserByEmail(String email) {
        String sql = "Select Top 1 *  from [User] where Email = ?";
        ConnectUtils db = ConnectUtils.getInstance();
        User userDTO = null ;
        try (Connection connection = db.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userDTO = new User();
                userDTO.setEmail(resultSet.getString("Email"));
                userDTO.setUserName(resultSet.getString("UserName"));
                userDTO.setUserId(resultSet.getInt("UserId"));
                userDTO.setRoleId(resultSet.getInt("RoleId"));
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
            try (Connection connection = db.openConnection();
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
        User user = null;
        ConnectUtils db = ConnectUtils.getInstance();

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setUserName(rs.getString("UserName"));
                user.setPassword(rs.getString("Password"));
                user.setEmail(rs.getString("Email"));
                user.setStatus(rs.getString("Status"));
                user.setRoleId(rs.getInt("RoleId"));
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setAddress(rs.getString("Address"));
                user.setBiography(rs.getString("Biography"));
                user.setCreateAt(rs.getTimestamp("CreateAt") != null ?
                        rs.getTimestamp("CreateAt").toLocalDateTime() : null);
                user.setDateOfBirth(rs.getDate("DateOfBirth") != null ?
                        rs.getDate("DateOfBirth").toLocalDate() : null);
                user.setProfilePicture(rs.getString("ProfilePicture"));
                logger.info("Đã tìm thấy người dùng với ID: {}", id);
            } else {
                logger.warn("Không tìm thấy người dùng với ID: {}", id);
            }

            rs.close();
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy thông tin người dùng với ID: {}", id, e);
            throw new RuntimeException("Lỗi khi lấy thông tin người dùng với ID: " + id, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public boolean updatePassword(String email, String password) {
        String hashPass = hashPassword(password);
        ConnectUtils db = ConnectUtils.getInstance();
        String sql = "UPDATE [User] SET [Password] = ? WHERE [Email] = ? ";
        try (Connection connection = db.openConnection();
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


    @Override
    public List<UserDTO> getCustomers() {
        String sql = "SELECT UserId, UserName, Email, PhoneNumber, Status, CreateAt FROM [User] WHERE RoleId = 2";
        List<UserDTO> result = new ArrayList<>();
        try (Connection con = ConnectUtils.getInstance().openConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setUserId(rs.getInt("UserId"));
                dto.setUserName(rs.getString("UserName"));
                dto.setEmail(rs.getString("Email"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));
                dto.setStatus(rs.getBoolean("Status"));

                Timestamp createAtTs = rs.getTimestamp("CreateAt");
                dto.setCreateAt(createAtTs != null ? createAtTs.toLocalDateTime() : null);

                result.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public List<UserDTO> getStaffWithRoleName() {
        String sql = "SELECT u.UserId, u.UserName, u.Email, u.PhoneNumber, u.Status, u.CreateAt, r.RoleName " +
                "FROM [User] u JOIN [Role] r ON u.RoleId = r.RoleId WHERE u.RoleId IN (3,4,5)";
        List<UserDTO> result = new ArrayList<>();
        try (Connection con = ConnectUtils.getInstance().openConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserDTO dto = new UserDTO();
                dto.setUserId(rs.getInt("UserId"));
                dto.setUserName(rs.getString("UserName"));
                dto.setEmail(rs.getString("Email"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));
                dto.setStatus(rs.getBoolean("Status"));
                Timestamp createAtTs = rs.getTimestamp("CreateAt");
                dto.setCreateAt(createAtTs != null ? createAtTs.toLocalDateTime() : null);
                dto.setRoleName(rs.getString("RoleName"));
                result.add(dto);            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean banUser(int userId) {
        return updateStatus(userId, 0);
    }

    @Override
    public boolean unbanUser(int userId) {
        return updateStatus(userId, 1);
    }

    private boolean updateStatus(int userId, int status) {
        String sql = "UPDATE [User] SET Status = ? WHERE UserId = ?";
        try (Connection con = ConnectUtils.getInstance().openConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean createStaffAccount(UserDTO userDTO) {
        String sql = "INSERT INTO [User] (UserName, RoleId, Email, Password, Status) VALUES (?, ?, ?, ?, 1)";
        try (Connection con = ConnectUtils.getInstance().openConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            String userName = userDTO.getEmail().split("@")[0];
            stmt.setString(1, userName);
            stmt.setInt(2, userDTO.getRoleID());
            stmt.setString(3, userDTO.getEmail());
            stmt.setString(4, hashPassword(userDTO.getPassword()));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE [User] SET " +
                "FirstName = ?, " +
                "LastName = ?, " +
                "Email = ?, " +
                "PhoneNumber = ?, " +
                "DateOfBirth = ?, " +
                "Address = ? " +
                "WHERE UserId = ?";

        jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getAddress(),
                user.getUserId()
        );
    }


}