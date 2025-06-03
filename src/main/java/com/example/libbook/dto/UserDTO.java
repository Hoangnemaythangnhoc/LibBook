package com.example.libbook.dto;

public class UserDTO {
    private int userId;
    private String userName;
    private String password;
    private String email;
    private boolean status;
    private int roleID;
    private String newpass;

    public UserDTO(int userId, String userName, String password, String email, boolean status, int roleID, String newpass) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = status;
        this.roleID = roleID;
        this.newpass = newpass;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO(){

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
}
