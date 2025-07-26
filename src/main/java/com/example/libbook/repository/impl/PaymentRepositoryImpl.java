package com.example.libbook.repository.impl;

import com.example.libbook.dto.PaymentDTO;
import com.example.libbook.entity.Payment;
import com.example.libbook.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private static DataSource dataSource;

    @Autowired
    public PaymentRepositoryImpl(DataSource dataSource) {
        PaymentRepositoryImpl.dataSource = dataSource;
        System.out.println("OrderRepositoryImpl: Initialized with DataSource: " + (dataSource != null ? "Yes" : "No"));
    }

    @Override
    public boolean insertPayment(PaymentDTO paymentRequest) {
            boolean success = false;
            LocalDate date = LocalDate.now();
            String sql = "{call InsertPaymentList(?, ?, ?, ?)}";

            try (Connection conn = dataSource.getConnection();
                 CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, paymentRequest.getTransCode());
                    stmt.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay()));
                stmt.setDouble(3, (paymentRequest.getAmount()));
                stmt.setBoolean(4, true);
                boolean hasResult = stmt.execute();
                if (hasResult) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        if (rs.next()) {
                            int resultCode = rs.getInt("Success");
                            String message = rs.getString("Message");
                            if (resultCode == 0) {
                                throw new RuntimeException(message); // Ném lỗi nếu Success = 0
                            }
                            return true;
                        }
                    }
                }
                return false; // Nếu không có ResultSet
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi chèn thanh toán: " + e.getMessage());
            }
    }
}