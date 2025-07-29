package com.example.libbook.service;

import com.example.libbook.dto.PaymentDTO;

public interface PaymentService {

    boolean addNewPayment(PaymentDTO paymentDTO);
}
