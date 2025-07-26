package com.example.libbook.repository;

import com.example.libbook.dto.PaymentDTO;
import com.example.libbook.entity.Payment;

import java.util.List;

public interface PaymentRepository {

//    boolean AddPayment(List<Payment> payments);
    boolean insertPayment(PaymentDTO paymentRequest);
}