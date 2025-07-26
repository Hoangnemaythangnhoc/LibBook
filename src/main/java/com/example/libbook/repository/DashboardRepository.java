package com.example.libbook.repository;

import com.example.libbook.dto.ChartData;
import com.example.libbook.dto.DashboardData;

import java.util.List;

public interface DashboardRepository {
    DashboardData getDashboardData(int days);
    List<ChartData> getRevenueData(int days, boolean groupByMonth);
    List<ChartData> getOrdersData(int days, boolean groupByMonth);
    List<ChartData> getUsersData(int days, boolean groupByMonth);
}