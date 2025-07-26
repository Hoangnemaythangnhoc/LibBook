package com.example.libbook.service;

import com.example.libbook.dto.DashboardData;
import com.example.libbook.dto.MultiChartData;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
    DashboardData getDashboardData(String timeType);
    MultiChartData getMultiChartData(String timeType);
}
