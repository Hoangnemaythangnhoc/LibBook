package com.example.libbook.repository.impl;

import com.example.libbook.entity.Coupon;
import com.example.libbook.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Coupon> rowMapper = new RowMapper<>() {
        @Override
        public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
            Coupon c = new Coupon();
            c.setCouponId(rs.getInt("CouponId"));
            c.setCode(rs.getString("Code"));
            c.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
            c.setQuantity(rs.getBigDecimal("Quantity"));
            c.setStartDate(rs.getTimestamp("StartDate") != null ? rs.getTimestamp("StartDate").toLocalDateTime() : null);
            c.setEndDate(rs.getTimestamp("EndDate") != null ? rs.getTimestamp("EndDate").toLocalDateTime() : null);
            c.setIsActive(rs.getBoolean("IsActive"));
            return c;
        }
    };

    @Override
    public List<Coupon> findAll(String search) {
        String sql = "SELECT * FROM Coupon WHERE Code LIKE ?";
        String searchValue = "%" + search + "%";
        return jdbcTemplate.query(sql, new Object[]{searchValue}, rowMapper);
    }

    @Override
    public Coupon findById(Integer id) {
        String sql = "SELECT * FROM Coupon WHERE CouponId = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM Coupon WHERE Code = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{code}, rowMapper);
    }



    @Override
    public int checkCouponCode(String code) {
        String sql = "SELECT DiscountPercent FROM Coupon " +
                "WHERE Code = ? " +
                "AND IsActive = 1 " +
                "AND Quantity > 0 " +
                "AND StartDate <= CURRENT_TIMESTAMP " +
                "AND EndDate >= CURRENT_TIMESTAMP";
        try {
            Integer discountPercent = jdbcTemplate.queryForObject(sql, new Object[]{code}, Integer.class);
            return discountPercent != null ? discountPercent : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0; // Return 0 if no valid coupon is found
        }
    }

    @Override
    public void updateAmountCoupon(int couponId, BigDecimal amount) {
        String sql = "UPDATE Coupon SET Quantity = ? WHERE CouponId = ?";
        double quantity = amount.subtract(BigDecimal.ONE).doubleValue();
        try {
            jdbcTemplate.update(sql, quantity, couponId);
        } catch (Exception e) {
            // Log the exception (recommended to use a proper logging framework like SLF4J)
            System.err.println("Error updating coupon amount: " + e.getMessage());
            throw new RuntimeException("Failed to update coupon amount", e);
        }
    }


    @Override
    public void save(Coupon coupon) {
        String sql = "INSERT INTO Coupon (Code, DiscountPercent, Quantity, StartDate, EndDate, IsActive) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                coupon.getCode(),
                coupon.getDiscountPercent(),
                coupon.getQuantity(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getIsActive());
    }

    @Override
    public void update(Coupon coupon) {
        String sql = "UPDATE Coupon SET Code = ?, DiscountPercent = ?, Quantity = ?, StartDate = ?, EndDate = ?, IsActive = ? " +
                "WHERE CouponId = ?";
        jdbcTemplate.update(sql,
                coupon.getCode(),
                coupon.getDiscountPercent(),
                coupon.getQuantity(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getIsActive(),
                coupon.getCouponId());
    }

    @Override
    public List<Coupon> findAllPaged(String search, int offset, int size) {
        String sql = "SELECT * FROM Coupon WHERE Code LIKE ? ORDER BY CouponId OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        String searchValue = "%" + search + "%";
        return jdbcTemplate.query(sql, new Object[]{searchValue, offset, size}, rowMapper);
    }

    @Override
    public int countAll(String search) {
        String sql = "SELECT COUNT(*) FROM Coupon WHERE Code LIKE ?";
        String searchValue = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, new Object[]{searchValue}, Integer.class);
    }

}