package com.example.libbook.dto;

public class DashboardData {
    private double revenue;
    private int orders;
    private String topBook;
    private int users;

    public DashboardData() {}

    public DashboardData(double revenue, int orders, String topBook, int users) {
        this.revenue = revenue;
        this.orders = orders;
        this.topBook = topBook;
        this.users = users;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public String getTopBook() {
        return topBook;
    }

    public void setTopBook(String topBook) {
        this.topBook = topBook;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }
}