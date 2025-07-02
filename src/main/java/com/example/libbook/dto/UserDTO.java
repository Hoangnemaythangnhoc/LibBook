package com.example.libbook.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private int userId;
    private String userName;
    private String password;
    private String email;
    private boolean status;
    private int roleID;
    private String newpass;
    private LocalDateTime createAt;
    private String phoneNumber;
    private String roleName;

    public UserDTO(int userId, String userName, String password, String email, boolean status, int roleID, String newpass) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = status;
        this.roleID = roleID;
        this.newpass = newpass;
    }

    public UserDTO(int userId, String userName, String password, String email, boolean status, int roleID, LocalDateTime createAt, String phoneNumber, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = status;
        this.roleID = roleID;
        this.createAt = createAt;
        this.phoneNumber = phoneNumber;
        this.roleName = roleName;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public LocalDateTime getCreateAt() { return createAt; }

    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRoleName() { return roleName; }

    public void setRoleName(String roleName) { this.roleName = roleName; }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", roleID=" + roleID +
                '}';
    }
}
