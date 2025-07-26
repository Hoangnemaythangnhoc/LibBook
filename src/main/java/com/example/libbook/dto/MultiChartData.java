package com.example.libbook.dto;

import java.util.List;

public class MultiChartData {
    private List<ChartData> revenue;
    private List<ChartData> orders;
    private List<ChartData> users;

    public MultiChartData() {}

    public MultiChartData(List<ChartData> revenue, List<ChartData> orders, List<ChartData> users) {
        this.revenue = revenue;
        this.orders = orders;
        this.users = users;
    }

    public List<ChartData> getRevenue() {
        return revenue;
    }

    public void setRevenue(List<ChartData> revenue) {
        this.revenue = revenue;
    }

    public List<ChartData> getOrders() {
        return orders;
    }

    public void setOrders(List<ChartData> orders) {
        this.orders = orders;
    }

    public List<ChartData> getUsers() {
        return users;
    }

    public void setUsers(List<ChartData> users) {
        this.users = users;
    }
}