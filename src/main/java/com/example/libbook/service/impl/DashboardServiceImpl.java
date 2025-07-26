package com.example.libbook.service.impl;

import com.example.libbook.dto.ChartData;
import com.example.libbook.dto.DashboardData;
import com.example.libbook.dto.MultiChartData;
import com.example.libbook.repository.DashboardRepository;
import com.example.libbook.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    private static final String[] VALID_TIME_TYPES = {"1day", "1week", "1month", "3months", "7months", "lastyear"};

    public DashboardData getDashboardData(String timeType) {
        validateTimeType(timeType);
        int days = getDaysForTimeType(timeType);
        return dashboardRepository.getDashboardData(days);
    }

    public MultiChartData getMultiChartData(String timeType) {
        validateTimeType(timeType);
        int days = getDaysForTimeType(timeType);
        boolean groupByMonth = timeType.equals("lastyear");

        List<ChartData> revenueData = dashboardRepository.getRevenueData(days, groupByMonth);
        List<ChartData> ordersData = dashboardRepository.getOrdersData(days, groupByMonth);
        List<ChartData> usersData = dashboardRepository.getUsersData(days, groupByMonth);

        return new MultiChartData(revenueData, ordersData, usersData);
    }

    private void validateTimeType(String timeType) {
        boolean isValid = false;
        for (String validType : VALID_TIME_TYPES) {
            if (validType.equals(timeType)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException("Invalid timeType. Must be one of: " + String.join(", ", VALID_TIME_TYPES));
        }
    }

    private int getDaysForTimeType(String timeType) {
        switch (timeType) {
            case "1day": return 1;
            case "1week": return 7;
            case "1month": return 30;
            case "3months": return 90;
            case "7months": return 210;
            case "lastyear": return 365;
            default: throw new IllegalArgumentException("Invalid timeType");
        }
    }
}