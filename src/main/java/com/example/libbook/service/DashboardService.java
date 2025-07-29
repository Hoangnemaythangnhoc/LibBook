package com.example.libbook.service;

import com.example.libbook.dto.DashboardData;
import com.example.libbook.dto.MultiChartData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DashboardService {
    DashboardData getDashboardData(String timeType);
    MultiChartData getMultiChartData(String timeType);
}
