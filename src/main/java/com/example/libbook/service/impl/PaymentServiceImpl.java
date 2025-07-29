package com.example.libbook.service.impl;

import com.example.libbook.dto.PaymentDTO;
import com.example.libbook.repository.PaymentRepository;
import com.example.libbook.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public boolean addNewPayment(PaymentDTO paymentDTO) {
        return paymentRepository.insertPayment(paymentDTO);
    }
}
