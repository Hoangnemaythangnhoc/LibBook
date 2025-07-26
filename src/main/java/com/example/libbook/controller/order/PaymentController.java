package com.example.libbook.controller.order;

import com.example.libbook.dto.PaymentDTO;
import com.example.libbook.entity.Payment;
import com.example.libbook.entity.ResponseWrapper;
import com.example.libbook.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<?> addNewPayment(@RequestBody PaymentDTO paymentData) {
        try {
            boolean savedPayment = paymentService.addNewPayment(paymentData);
            return ResponseEntity.ok(new ResponseWrapper(savedPayment, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper(false, "Error processing payment: " + e.getMessage()));
        }
    }
}