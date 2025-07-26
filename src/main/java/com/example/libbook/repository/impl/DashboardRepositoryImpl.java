package com.example.libbook.repository.impl;

import com.example.libbook.dto.ChartData;
import com.example.libbook.dto.DashboardData;
import com.example.libbook.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public DashboardData getDashboardData(int days) {
        String sql = "SELECT COALESCE(SUM(od.Price * od.Quantity), 0) AS revenue, " +
                "COUNT(DISTINCT o.OrderId) AS orders, " +
                "(SELECT TOP 1 p.ProductName " +
                "FROM [OrderDetail] od2 " +
                "JOIN [Product] p ON od2.ProductId = p.ProductId " +
                "JOIN [Order] o2 ON od2.OrderId = o2.OrderId " +
                "WHERE o2.CreateDate >= DATEADD(DAY, ?, GETDATE()) " +
                "GROUP BY p.ProductName ORDER BY SUM(od2.Quantity) DESC) AS topBook, " +
                "COUNT(DISTINCT u.UserId) AS users " +
                "FROM [Order] o " +
                "JOIN [OrderDetail] od ON o.OrderId = od.OrderId " +
                "JOIN [User] u ON o.UserId = u.UserId " +
                "WHERE o.CreateDate >= DATEADD(DAY, ?, GETDATE())";
        return jdbcTemplate.queryForObject(sql, new Object[]{-days, -days}, (rs, rowNum) ->
                new DashboardData(
                        rs.getDouble("revenue"),
                        rs.getInt("orders"),
                        rs.getString("topBook"),
                        rs.getInt("users")
                ));
    }

    @Override
    public List<ChartData> getRevenueData(int days, boolean groupByMonth) {
        String groupBy = groupByMonth ? "FORMAT(o.CreateDate, 'yyyy-MM')" : "t.TagName";
        String orderBy = groupByMonth ? "ORDER BY FORMAT(o.CreateDate, 'yyyy-MM')" : "";
        String sql = "SELECT COALESCE(" + groupBy + ", 'Unknown') AS x, " +
                "COALESCE(SUM(od.Price * od.Quantity), 0) AS y " +
                "FROM [Order] o " +
                "LEFT JOIN [OrderDetail] od ON o.OrderId = od.OrderId " +
                "LEFT JOIN [ProductTag] pt ON od.ProductId = pt.ProductId " +
                "LEFT JOIN [Tag] t ON pt.TagId = t.TagId " +
                "WHERE o.CreateDate >= DATEADD(DAY, ?, GETDATE()) " +
                "GROUP BY " + groupBy + " " + orderBy;
        return jdbcTemplate.query(sql, new Object[]{-days}, (rs, rowNum) ->
                new ChartData(rs.getString("x"), rs.getDouble("y")));
    }

    @Override
    public List<ChartData> getOrdersData(int days, boolean groupByMonth) {
        String groupBy = groupByMonth ? "FORMAT(o.CreateDate, 'yyyy-MM')" : "t.TagName";
        String orderBy = groupByMonth ? "ORDER BY FORMAT(o.CreateDate, 'yyyy-MM')" : "";
        String sql = "SELECT COALESCE(" + groupBy + ", 'Unknown') AS x, " +
                "COALESCE(COUNT(DISTINCT o.OrderId), 0) AS y " +
                "FROM [Order] o " +
                "LEFT JOIN [OrderDetail] od ON o.OrderId = od.OrderId " +
                "LEFT JOIN [ProductTag] pt ON od.ProductId = pt.ProductId " +
                "LEFT JOIN [Tag] t ON pt.TagId = t.TagId " +
                "WHERE o.CreateDate >= DATEADD(DAY, ?, GETDATE()) " +
                "GROUP BY " + groupBy + " " + orderBy;
        return jdbcTemplate.query(sql, new Object[]{-days}, (rs, rowNum) ->
                new ChartData(rs.getString("x"), rs.getDouble("y")));
    }

    @Override
    public List<ChartData> getUsersData(int days, boolean groupByMonth) {
        String groupBy = groupByMonth ? "FORMAT(u.CreateAt, 'yyyy-MM')" : "t.TagName";
        String orderBy = groupByMonth ? "ORDER BY FORMAT(u.CreateAt, 'yyyy-MM')" : "";
        String sql = "SELECT COALESCE(" + groupBy + ", 'Unknown') AS x, " +
                "COALESCE(COUNT(DISTINCT u.UserId), 0) AS y " +
                "FROM [User] u " +
                "LEFT JOIN [Order] o ON u.UserId = o.UserId " +
                "LEFT JOIN [OrderDetail] od ON o.OrderId = od.OrderId " +
                "LEFT JOIN [ProductTag] pt ON od.ProductId = pt.ProductId " +
                "LEFT JOIN [Tag] t ON pt.TagId = t.TagId " +
                "WHERE u.CreateAt >= DATEADD(DAY, ?, GETDATE()) " +
                "GROUP BY " + groupBy + " " + orderBy;
        return jdbcTemplate.query(sql, new Object[]{-days}, (rs, rowNum) ->
                new ChartData(rs.getString("x"), rs.getDouble("y")));
    }
}